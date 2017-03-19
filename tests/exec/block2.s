	.text
	.globl main
main:
	movq $65, %rdi
	movq %rdi, %rcx
	movq %rcx, %rdi
	call putchar
	movq $0, %rdi
	jz L18
	movq $66, %rdi
	movq %rdi, y
	movq y, %rdi
	call putchar
L6:
	movq %rcx, %rdi
	call putchar
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
L18:
	movq $67, %rdi
	movq %rdi, y
	movq $68, %rdi
	movq %rdi, z
	movq y, %rdi
	call putchar
	movq z, %rdi
	call putchar
	jmp L6
	.data
