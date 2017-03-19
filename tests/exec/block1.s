	.text
	.globl main
main:
	movq $65, %rdi
	call putchar
	movq $1, %rdi
	jz L14
	movq $66, %rdi
	movq %rdi, y
	movq y, %rdi
	call putchar
L6:
	call putchar
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
L14:
	movq $67, %rdi
	movq %rdi, z
	movq z, %rdi
	call putchar
	jmp L6
	jmp L6
	.data
