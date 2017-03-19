	.text
	.globl main
main:
	movq $65, %rdi
	movq %rdi, %rcx
	movq %rcx, %rdi
	call putchar
	movq %rcx, %rdi
	addq $1, %rdi
	movq %rdi, %rcx
	movq %rcx, %rdi
	call putchar
	movq %rcx, %rdi
	addq $1, %rdi
	movq %rdi, %rsi
	movq %rcx, %rdi
	call putchar
	movq %rsi, %rdi
	call putchar
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
	.data
