open Ast
open Format

(* Exception lev�e pour signaler une erreur pendant l'interpr�tation *)
exception Error of string
let error s = raise (Error s)

(* Les valeurs de Mini-Python

   - une diff�rence notable avec Python : on
     utilise ici le type int alors que les entiers de Python sont de
     pr�cision arbitraire ; on pourrait utiliser le module Big_int d'OCaml
     mais on chosisit la facilit�
   - ce que Python appelle une liste est en r�alit� un tableau
     redimensionnable ; dans le fragment consid�r� ici, il n'y a pas
     de possibilit� d'en modifier la longueur, donc un simple tableau OCaml
     convient *)
type value =
  | Vnone
  | Vbool of bool
  | Vint of int
  | Vstring of string
  | Vlist of value array

(* Affichage d'une valeur sur la sortie standard *)
let rec print_value = function
  | Vnone -> printf "None"
  | Vbool true -> printf "True"
  | Vbool false -> printf "False"
  | Vint n -> printf "%d" n
  | Vstring s -> printf "%s" s
  | Vlist a ->
    let n = Array.length a in
    printf "[";
    for i = 0 to n-1 do print_value a.(i); if i < n-1 then printf ", " done;
    printf "]"

(* Interpr�tation bool�enne d'une valeur

   En Python, toute valeur peut �tre utilis�e comme un bool�en : None,
   la liste vide, la cha�ne vide et l'entier 0 sont consid�r�s comme
   False et toute autre valeurs comme True *)

let is_false = function
  | Vnone
  | Vbool false
  | Vstring ""
  | Vlist [||] -> true
  | Vint n -> n = 0
  | _ -> false

let is_true v = not (is_false v)

(* Interpr�tation des op�rateurs binaires

   - l'op�rateur + est surcharg� : il d�signe aussi la concat�nation
     des cha�nes et des listes
   - les op�rateurs / et % doivent lever une exception si on tente de diviser
     par z�ro
   - pour les comparaisons structurelles, on utilisera celles d'OCaml, m�me
     si elles ne co�ncident pas exactement avec celles de Python
*)

let binop op v1 v2 = match op, v1, v2 with
  | Badd, Vint n1, Vint n2 -> Vint (n1+n2)
  | Badd, Vstring s1, Vstring s2 -> Vstring (s1 ^ s2)
  | Badd, Vlist l1, Vlist l2 -> Vlist (Array.append l1 l2)
  | Bsub, Vint n1, Vint n2 -> Vint (n1-n2)
  | Bmul, Vint n1, Vint n2 -> Vint (n1*n2)
  | (Bdiv | Bmod), Vint _, Vint 0 -> error "division by zero"
  | Bdiv, Vint n1, Vint n2 -> Vint (n1/n2)
  | Bmod, Vint n1, Vint n2 -> Vint (n1 mod n2)
  | Beq, _, _ -> Vbool (v1 = v2)
  | Bneq, _, _ -> Vbool (v1 <> v2)
  | Blt, _, _ -> Vbool (v1 < v2)
  | Ble, _, _ -> Vbool (v1 <= v2)
  | Bgt, _, _ -> Vbool (v1 > v2)
  | Bge, _, _ -> Vbool (v1 >= v2)
  | _ -> error "unsupported operand types"

(* Les fonctions sont ici uniquement globales *)

let functions = (Hashtbl.create 17 : (string, ident list * stmt) Hashtbl.t)

(* L'instruction 'return' de Python est interpr�t�e � l'aide d'une exception *)

exception Return of value

(* Les variables locales (param�tres de fonctions et variables introduites
   par des affectations) sont stock�es dans une table de hachage pass�e en
   arguments aux fonctions suivantes sous le nom 'ctx' *)

type ctx = (string, value) Hashtbl.t

(* Interpr�tation d'une expression (renvoie une valeur) *)

let rec expr ctx = function
  | Ecst Cnone ->
      Vnone
  | Ecst (Cbool b) ->
      Vbool b
  | Ecst (Cint n) ->
      Vint n
  | Ecst (Cstring s) ->
      Vstring s
  | Ebinop (Band, e1, e2) ->
      let v1 = expr ctx e1 in
      if is_true v1 then expr ctx e2 else v1
  | Ebinop (Bor, e1, e2) ->
      let v1 = expr ctx e1 in
      if is_false v1 then expr ctx e2 else v1
  | Ebinop (Badd | Bsub | Bmul | Bdiv | Bmod |
            Beq | Bneq | Blt | Ble | Bgt | Bge as op, e1, e2) ->
      binop op (expr ctx e1) (expr ctx e2)
  | Eunop (Uneg, e1) ->
      begin match expr ctx e1 with
        | Vint n -> Vint (-n)
        | _ -> error "unsupported operand types" end
  | Eunop (Unot, e1) ->
      Vbool (is_false (expr ctx e1))
  | Ecall ("len", [e1]) ->
      begin match expr ctx e1 with
        | Vstring s -> Vint (String.length s)
        | Vlist l -> Vint (Array.length l)
        | _ -> error "this value has no 'len'" end
  | Ecall ("range", [e1]) ->
      let n = expr_int ctx e1 in
      Vlist (Array.init (max 0 n) (fun i -> Vint i))
  | Ecall (f, el) ->
      if not (Hashtbl.mem functions f) then error ("unbound function " ^ f);
      let args, body = Hashtbl.find functions f in
      if List.length args <> List.length el then error "bad arity";
      let ctx' = Hashtbl.create 17 in
      List.iter2 (fun x e -> Hashtbl.add ctx' x (expr ctx e)) args el;
      begin try stmt ctx' body; Vnone with Return v -> v end
  | Elist el ->
      Vlist (Array.of_list (List.map (expr ctx) el))
  | Eleft lv ->
      right_value ctx lv

(* interpr�tation d'une valeur droite *)

and right_value ctx = function
  | Lident id ->
      if not (Hashtbl.mem ctx id) then error "unbound variable";
      Hashtbl.find ctx id
  | Lnth (e1, e2) ->
      let l = expr_list ctx e1 in
      let i = expr_int ctx e2 in
      if i < 0 || i >= Array.length l then error "index out of bounds";
      l.(i)

(* interpr�tation d'une valeur et v�rification qu'il s'agit d'un entier *)

and expr_int ctx e = match expr ctx e with
  | Vint n -> n
  | _ -> error "integer expected"

and expr_list ctx e = match expr ctx e with
  | Vlist l -> l
  | _ -> error "list expected"

(* interpr�tation d'une instruction ; ne renvoie rien *)

and stmt ctx = function
  | Sif (e, s1, s2) ->
      if is_true (expr ctx e) then stmt ctx s1 else stmt ctx s2
  | Sreturn e ->
      raise (Return (expr ctx e))
  | Sassign (Lident id, e1) ->
      Hashtbl.replace ctx id (expr ctx e1)
  | Sassign (Lnth (e1, e2), e3) ->
      let l = expr_list ctx e1 in
      let i = expr_int ctx e2 in
      if i < 0 || i >= Array.length l then error "index out of bounds";
      l.(i) <- expr ctx e3
  | Sprint e ->
      print_value (expr ctx e); printf "@."
  | Sblock bl ->
      block ctx bl
  | Sfor (x, e, s) ->
      let l = expr_list ctx e in
      Array.iter (fun v -> Hashtbl.replace ctx x v; stmt ctx s) l
  | Seval e ->
      ignore (expr ctx e)

(* interpr�tation d'un bloc i.e. d'une s�quence d'instructions *)

and block ctx = function
  | [] -> ()
  | s :: sl -> stmt ctx s; block ctx sl

(* interpr�tation d'un fichier *)

let file (fl, s) =
  List.iter
    (fun (f,args,body) -> Hashtbl.add functions f (args, body)) fl;
  stmt (Hashtbl.create 17) s

