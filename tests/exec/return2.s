	.text
	.globl main
f:
	movq %rcx, %rax
	movq $2, %rdi
	imulq %rdi, %rax
	movq %rcx, %rdi
	addq %rdi, %rax
	ret
main:
	movq $0, %rdi
	movq $65, %rdi
	call f
	call putchar
	movq $1, %rdi
	movq $65, %rdi
	call f
	call putchar
	movq $2, %rdi
	movq $65, %rdi
	call f
	call putchar
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
	.data
