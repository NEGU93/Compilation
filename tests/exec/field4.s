	.text
	.globl main
main:
	movq S, %rcx
	call sizeof
	call sbrk
	movq %rdi, %rcx
	movq $65, %rdi
	movq %rdi, 0(%rcx) 
	movq 0(%rcx) , %rdi
	call putchar
	movq $66, %rdi
	movq %rdi, 1(%rcx) 
	movq 1(%rcx) , %rdi
	call putchar
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
	.data
