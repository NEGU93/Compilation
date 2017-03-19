	.text
	.globl main
main:
	movq $0, %rdi
	movq %rdi, %rcx
	movq 0(%rcx) , %rdi
	call putchar
	ret
	.data
