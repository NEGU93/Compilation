
open Ast
open Format

(* Exception levée pour signaler une erreur pendant l'interprétation *)
exception Error of string
let error s = raise (Error s)

(* Les valeurs de Mini-Python

   - une différence notable avec Python : on
     utilise ici le type int alors que les entiers de Python sont de
     précision arbitraire ; on pourrait utiliser le module Big_int d'OCaml
     mais on choisit la facilité
   - ce que Python appelle une liste est en réalité un tableau
     redimensionnable ; dans le fragment considéré ici, il n'y a pas
     de possibilité d'en modifier la longueur, donc un simple tableau OCaml
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

(* Interprétation booléenne d'une valeur

   En Python, toute valeur peut être utilisée comme un booléen : None,
   la liste vide, la chaîne vide et l'entier 0 sont considérés comme
   False et toute autre valeurs comme True *)

let is_false v = assert false (* à compléter (question 2) *)

let is_true v = assert false (* à compléter (question 2) *)

(* Les fonctions sont ici uniquement globales *)

let functions = (Hashtbl.create 17 : (string, ident list * stmt) Hashtbl.t)

(* L'instruction 'return' de Python est interprétée à l'aide d'une exception *)

exception Return of value

(* Les variables locales (paramètres de fonctions et variables introduites
   par des affectations) sont stockées dans une table de hachage passée en
   arguments aux fonctions suivantes sous le nom 'ctx' *)

type ctx = (string, value) Hashtbl.t

(* Interprétation d'une expression (renvoie une valeur) *)

let rec expr ctx = function
  | Ecst Cnone ->
      Vnone
  | Ecst (Cstring s) ->
      Vstring s
  (* arithmétique *)
  | Ecst (Cint n) ->
      assert false (* à compléter (question 1) *)
  | Ebinop (Badd | Bsub | Bmul | Bdiv | Bmod |
            Beq | Bneq | Blt | Ble | Bgt | Bge as op, e1, e2) ->
      let v1 = expr ctx e1 in
      let v2 = expr ctx e2 in
      begin match op, v1, v2 with
        | Badd, Vint n1, Vint n2 -> assert false (* à compléter (question 1) *)
        | Bsub, Vint n1, Vint n2 -> assert false (* à compléter (question 1) *)
        | Bmul, Vint n1, Vint n2 -> assert false (* à compléter (question 1) *)
        | Bdiv, Vint n1, Vint n2 -> assert false (* à compléter (question 1) *)
        | Bmod, Vint n1, Vint n2 -> assert false (* à compléter (question 1) *)
        | Beq, _, _  -> assert false (* à compléter (question 2) *)
        | Bneq, _, _ -> assert false (* à compléter (question 2) *)
        | Blt, _, _  -> assert false (* à compléter (question 2) *)
        | Ble, _, _  -> assert false (* à compléter (question 2) *)
        | Bgt, _, _  -> assert false (* à compléter (question 2) *)
        | Bge, _, _  -> assert false (* à compléter (question 2) *)
        | Badd, Vstring s1, Vstring s2 ->
            assert false (* à compléter (question 3) *)
        | Badd, Vlist l1, Vlist l2 ->
            assert false (* à compléter (question 5) *)
        | _ -> error "unsupported operand types"
      end
  | Eunop (Uneg, e1) ->
      assert false (* à compléter (question 1) *)
  (* booléens *)
  | Ecst (Cbool b) ->
      assert false (* à compléter (question 2) *)
  | Ebinop (Band, e1, e2) ->
      assert false (* à compléter (question 2) *)
  | Ebinop (Bor, e1, e2) ->
      assert false (* à compléter (question 2) *)
  | Eunop (Unot, e1) ->
      assert false (* à compléter (question 2) *)
  (* appel de fonction *)
  | Ecall ("len", [e1]) ->
      assert false (* à compléter (question 5) *)
  | Ecall ("range", [e1]) ->
      assert false (* à compléter (question 5) *)
  | Ecall (f, el) ->
      assert false (* à compléter (question 4) *)
  | Eleft (Lident id) ->
      assert false (* à compléter (question 3) *)
  | Elist el ->
      assert false (* à compléter (question 5) *)
  | Eleft (Lnth (e1, e2)) ->
      assert false (* à compléter (question 5) *)

(* interprétation d'une instruction ; ne renvoie rien *)

and stmt ctx = function
  | Seval e ->
      ignore (expr ctx e)
  | Sprint e ->
      print_value (expr ctx e); printf "@."
  | Sblock bl ->
      block ctx bl
  | Sif (e, s1, s2) ->
      assert false (* à compléter (question 2) *)
  | Sassign (Lident id, e1) ->
      assert false (* à compléter (question 3) *)
  | Sreturn e ->
      assert false (* à compléter (question 4) *)
  | Sfor (x, e, s) ->
      assert false (* à compléter (question 5) *)
  | Sassign (Lnth (e1, e2), e3) ->
      assert false (* à compléter (question 5) *)

(* interprétation d'un bloc i.e. d'une séquence d'instructions *)

and block ctx = function
  | [] -> ()
  | s :: sl -> stmt ctx s; block ctx sl

(* interprétation d'un fichier *)

let file (fl, s) =
  List.iter
    (fun (f,args,body) -> Hashtbl.add functions f (args, body)) fl;
  stmt (Hashtbl.create 17) s



