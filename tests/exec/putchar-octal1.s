	.text
	.globl main
main:
	movq $65, %rdi
	call putchar
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
	.data
