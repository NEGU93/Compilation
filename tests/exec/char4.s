	.text
	.globl main
f:
	movq %rcx, %rdi
	addq %rdi, %rax
	ret
main:
	movq $0, %rsi
	movq $65, %rdi
	call f
	movq %rax, %rdi
	call putchar
	movq $1, %rsi
	movq $65, %rdi
	call f
	movq %rax, %rdi
	call putchar
	movq $2, %rsi
	movq $65, %rdi
	call f
	movq %rax, %rdi
	call putchar
	movq $3, %rsi
	movq $65, %rdi
	call f
	movq %rax, %rdi
	call putchar
	addq $1, %rdi
	call putchar
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
	.data
