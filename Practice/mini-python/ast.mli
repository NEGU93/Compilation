
(* Arbres de syntaxe abstraite de Mini-Python *)

type ident = string

type unop =
  | Uneg | Unot

type binop =
  | Badd | Bsub | Bmul | Bdiv | Bmod
  | Beq | Bneq | Blt | Ble | Bgt | Bge (* comparaison structurelle *)
  | Band | Bor (* paresseux *)

type constant =
  | Cnone
  | Cbool of bool
  | Cstring of string
  | Cint of int (* en Python les entiers sont en réalité de précision
                   arbitraire; on simplifie ici *)

type expr =
  | Ecst of constant
  | Ebinop of binop * expr * expr
  | Eunop of unop * expr
  | Ecall of ident * expr list
  | Elist of expr list
  | Eleft of left_value

and left_value =
  | Lident of ident
  | Lnth of expr * expr

and stmt =
  | Sif of expr * stmt * stmt
  | Sreturn of expr
  | Sassign of left_value * expr
  | Sprint of expr
  | Sblock of stmt list
  | Sfor of ident * expr * stmt
  | Seval of expr

and def = ident * ident list * stmt

and file = def list * stmt

