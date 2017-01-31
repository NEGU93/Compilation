#########################################################################################
# Compiler le programme suivant :							#
#											#
#	#include <stdio.h>								#
#											#
#	int isqrt(int n) {								#
#  		int c = 0, s = 1;							#
#  		while (s <= n) {							#
#    			c++;								#
#    			s += 2*c + 1;							#
# 		}									#
#  		return c;								#
#	}										#
#											#
#	int main() {									#
#  		int n;									#
#  		for (n = 0; n <= 20; n++)						#
#    			printf("sqrt(%2d) = %2d\n", n, isqrt(n));			#
#  		return 0;								#
#	}										#
#											#
# On essayera notamment d'effectuer les optimisations suivantes :			#
#											#
#    une seule instruction de branchement par tour de boucle ;				#
#    une seule instruction assembleur pour compiler l'instruction s += 2*c + 1. 	#
#########################################################################################

	.text
	.globl main
	.globl isqrt
main:
	xorq	%rsi, 	%rsi	# n = %rsi = 0
	jmp	L2M
L1M:	
	call	isqrt
	movq	%rax,	%rdx
	xorq	%rax,	%rax
	pushq	%rsi
	movq	$msg,	%rdi
	call	printf
	popq	%rsi
	incq	%rsi
L2M:
	cmpq	$21,	%rsi	# n == 20 ?	
	jne	L1M
	
	xorq	%rax,	%rax	# return 0;
	ret	

isqrt:
	pushq	%r12		# s = %r12 (I know main doesn't use it but... good programming)
	movq	$1,	%r12
	xorq	%rax,	%rax	# c = %rax because c is the answer
	jmp	L2
L1:				# Body of the while
	incq	%rax		# c++
	leaq	1(%r12, %rax, 2), %r12
L2:	
	cmpq	%r12,	%rsi	# rsi - r12 = n - s (condition of the while)
	jg	L1

	popq	%r12
	ret


	.data
msg:
	.string	"sqrt(%2d) = %2d\n"
	

