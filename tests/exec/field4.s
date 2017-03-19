	.text
	.globl main
main:
	movq S, %rdi
	call sizeof
	movq %rax, %rdi
	call sbrk
	movq $65, %rdi
	movq %rdi, 0(%rax) 
	movq 0(%rax) , %rdi
	call putchar
	movq $66, %rdi
	movq %rdi, 1(%rax) 
	movq 1(%rax) , %rdi
	call putchar
	movq $10, %rdi
	call putchar
	movq $0, %rax
	ret
	.data
