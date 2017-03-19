	.text
	.globl main
main:
	movq $65, %rdi
	call putchar
	jz L38
	movq $66, %rdi
L38:
	call putchar
	jz L30
	movq $0, %rdi
	jz L30
	movq $67, %rdi
L30:
	call putchar
	jz L22
	movq $1, %rdi
	jz L22
	movq $68, %rdi
L22:
	call putchar
	jz L18
	movq $69, %rdi
	call putchar
	jz L10
	movq $70, %rdi
	call putchar
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
L10:
	movq $1, %rdi
	jz L6
	jmp L8
L18:
	movq $0, %rdi
	jz L14
	jmp L16
	jmp L22
	jmp L22
	jmp L30
	jmp L30
	jmp L38
	.data
