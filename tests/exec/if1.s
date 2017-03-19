	.text
	.globl main
main:
	movq $65, %rdi
	movq %rdi, %rcx
	movq %rcx, %rdi
	call putchar
	movq %rcx, %rdi
	jz L38
	movq $66, %rdi
	movq %rdi, %rcx
L38:
	movq %rcx, %rdi
	call putchar
	movq %rcx, %rdi
	jz L30
	movq $0, %rdi
	jz L30
	movq $67, %rdi
	movq %rdi, %rcx
L30:
	movq %rcx, %rdi
	call putchar
	movq %rcx, %rdi
	jz L22
	movq $1, %rdi
	jz L22
	movq $68, %rdi
	movq %rdi, %rcx
L22:
	movq %rcx, %rdi
	call putchar
	movq %rcx, %rdi
	jz L18
	movq $69, %rdi
	movq %rdi, %rcx
	movq %rcx, %rdi
	call putchar
	movq %rcx, %rdi
	jz L10
	movq $70, %rdi
	movq %rdi, %rcx
	movq %rcx, %rdi
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
