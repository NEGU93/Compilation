	.text
	.globl main
main:
	movq $104, %rdi
	call putchar
	movq $101, %rdi
	call putchar
	movq $2, %rdi
	movq $8, %rcx
	idivq %rcx, %rdi
	addq $100, %rdi
	call putchar
	movq $108, %rdi
	call putchar
	movq $111, %rdi
	call putchar
	movq $32, %rdi
	call putchar
	movq $0, %rdi
	movq $-1, %rcx
	subq %rcx, %rdi
	addq $118, %rdi
	call putchar
	movq $11, %rdi
	addq $100, %rdi
	call putchar
	movq $1, %rdi
	addq $113, %rdi
	call putchar
	movq $0, %rdi
	addq $108, %rdi
	call putchar
	movq $2, %rdi
	movq $2, %rcx
	cmpq %rcx, %rdi
	je L8
	movq $0, %rdi
L7:
	addq $99, %rdi
	call putchar
	movq $0, %rdi
	addq $10, %rdi
	call putchar
	movq $0, %rax
	ret
L8:
	movq $1, %rdi
	jmp L7
	.data
