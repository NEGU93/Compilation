	.text
	.globl main
main:
	movq $0, %rdi
	jz L16
	movq $0, %rdi
L14:
	addq $65, %rdi
	call putchar
	movq $1, %rdi
	jz L8
	movq $0, %rdi
L6:
	addq $65, %rdi
	call putchar
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
L8:
	movq $1, %rdi
	jmp L6
L16:
	movq $1, %rdi
	jmp L14
	.data
