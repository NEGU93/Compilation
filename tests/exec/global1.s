	.text
	.globl main
main:
	movq $65, %rdi
	movq %rdi, x
	movq x, %rdi
	call putchar
	movq x, %rdi
	addq $1, %rdi
	movq %rdi, x
	movq x, %rdi
	call putchar
	movq x, %rdi
	addq $2, %rdi
	movq %rdi, x
	movq x, %rdi
	call putchar
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
	.data
x:
y:
