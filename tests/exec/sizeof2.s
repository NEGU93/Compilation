	.text
	.globl main
main:
	movq S, %rdi
	call sizeof
	movq %rax, %rdi
	addq $65, %rdi
	call putchar
	movq $10, %rdi
	call putchar
	movq T, %rdi
	call sizeof
	movq %rax, %rdi
	addq $65, %rdi
	call putchar
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
	.data
