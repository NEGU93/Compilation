	.text
	.globl main
main:
	movq $0, %rdi
	jz L14
	movq $0, %rcx
L12:
	addq $65, %rdi
	call putchar
	movq $1, %rdi
	jz L8
	movq $0, %rcx
L6:
	addq $65, %rdi
	call putchar
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
L8:
	movq $1, %rcx
	jmp L6
L14:
	movq $1, %rcx
	jmp L12
	.data
