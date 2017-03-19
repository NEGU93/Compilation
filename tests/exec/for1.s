	.text
	.globl main
main:
	movq $0, %rdi
	movq %rdi, %rdx
	movq $0, %rdi
	movq %rdi, %rsi
L28:
	movq %rsi, %rdi
	movq $10, %rcx
	cmpq %rdi, %rcx
	jl L25
	movq %rdx, %rdi
	movq $100, %rcx
	cmpq %rdi, %rcx
	je L6
L4:
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
L6:
	movq $33, %rdi
	call putchar
	jmp L4
	jmp L4
L25:
	movq $10, %rdi
	movq %rdi, j
L23:
	movq j, %rdi
	movq $0, %rcx
	cmpq %rdi, %rcx
	jg L20
	movq %rsi, %rdi
	addq $1, %rdi
	movq %rdi, %rsi
	jmp L28
	jmp L28
L20:
	movq %rdx, %rdi
	addq $1, %rdi
	movq %rdi, %rdx
	movq j, %rdi
	addq $-1, %rdi
	movq %rdi, j
	jmp L23
	jmp L23
	.data
