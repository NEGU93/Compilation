	.text
	.globl main
print_int:
	movq $10, %rdi
	movq %rcx, %rdi
	idivq %rdi,%rdi
	movq $9, %rdi
	cmpq %rcx, %rdi
	jg L11
L9:
	movq $10, %rsi
	imulq %rsi, %rdi
	subq %rdi, %rcx
	addq $48, %rdi
	call putchar
	movq $0, %rax
	ret
L11:
	call print_int
	jmp L9
	jmp L9
main:
	movq $42, %rdi
	call print_int
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
	.data
