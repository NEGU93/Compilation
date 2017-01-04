#################################################
# Compile the following program:		#
#						#
#	#include <stdio.h>			#
#						#
#	int main() {				#
#	  	printf("n = %d\n", 42);		#
#		  return 0;			#
#	}					#
#################################################
	
	.text
	.globl main
main:				# Going to use %rdi and %rsi as parameters for printf
	movq	$msg,	%rdi	# first parameter
	movq	$42,	%rsi	# second parameter
	xorq	%rax,	%rax	# rax = 0 because there are no parameters given by the register 'vecteurs'
	call	printf
	xorq	%rax,	%rax	# rax = 0 (necessary?)
	ret
	
	
	.data
msg:
	.string	"n = %d\n"

			
