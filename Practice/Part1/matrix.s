#################################################################
# Problem extracted from https://projecteuler.net/problem=345   #
#################################################################

	.text
	.globl	main
	.globl	f

main:
	movq	$0,	%rdi	# i = 0
	movq	$1,	%rsi	
	salq	$N,	%rsi	# (1 << N)	
	decq	%rsi
	call	f

	movq	%rax,	rsi	# variable f(0, (1 << N) - 1)
	movq	$msg,	rdi	# variable "solution = %d\n"
	xorq	%rax,	%rax	# no register "vectours"
	call	printf		# make the print

	xorq	%rax,	%rax	#return 0;
	ret

f:	
	pushq	%rbx		# this will be x
	pushq	%r12		# key
	pushq	%r13		# r
	pushq	%r14		# j
	pushq	%r15		# col
	
	testq	$N,	%rdi	# if (i == N)
	je	RET0:		# return 0;

	movq	%rsi,	%r12	# key = c
	salq	$L,	%r12	# key = c << L
	orq	%rdi,	%r12	# key = c << L | i

	movq	(memo, %r12),	%r13	# r = memo[key] ???
	jnz	RETR	 	# if (r != 0)

	xorq	%rax,	%rax	# s = 0
	xorq	$-1,	%r14	# j = -1
	jmp	L2
L1:
	movq	$1,	%r15	# col = 1
	salq	%r14,	%r15	# col = 1 << j
	
	movq	%r15,	%rcx	# have to use a new register :(
	addq	%rsi,	%rcx	# c & col
	jz	L2		# if ((c & col) == 0) continue;

	leaq	(%r14, %rdi, 15), %rcx 	# 15*i + j
	movq	(m, %rcx, 4), 	  %rbx	# x = m[i][j] = m + 4 * (15*i + j)
	
	pushq	%rax
	pushq	%rdi		# Instead of push and pop I can add and substract
	pushq	%rsi		# This way is slower but fancier (2 more inst, plus I think push and pop are slower)
	incq	%rdi		# i += 1
	subq	%r15,	%rsi	# c - col
	call	f
	popq	%rsi
	popq	%rdi
	addq	%rax,	%rbx	# x = m[i][j] + f(i + 1, c - col)
	popq	%rax

	compq	%rbx,	%rax	# s - n
	jge	L2		# if (x > s)
	movq	%rbx,	%rax	# s = x
	jmp	L2	
		
L2:	
	incq	%r14		# this will make j = 0 in the first loop
	testq	$r14,	%N
	jl	L1

	movq	%rax,	(memo, %r12) # memo[key] = s

RET:	
	popq	%r15
	popq	%r14
	popq	%r13
	popq	%r12
	popq	%rbx
	ret
RET0:	
	xorq	%rax,	%rax	#return 0
	jmp	RET		#would it be to ugly to put RET code here? (memory vs speed)
RETR:	
	movq	%r13,	%rax
	jmp	RET

	.data
msg:	.string	"solution = %d\n"
N:	.long	15
L:	.long	4
m:
	.long	7
	.long	53
	.long	183
	.long	439
	.long	863
	.long	497
	.long	383
	.long	563
	.long	79
	.long	973
	.long	287
	.long	63
	.long	343
	.long	169
	.long	583
	.long	627
	.long	343
	.long	773
	.long	959
	.long	943
	.long	767
	.long	473
	.long	103
	.long	699
	.long	303
	.long	957
	.long	703
	.long	583
	.long	639
	.long	913
	.long	447
	.long	283
	.long	463
	.long	29
	.long	23
	.long	487
	.long	463
	.long	993
	.long	119
	.long	883
	.long	327
	.long	493
	.long	423
	.long	159
	.long	743
	.long	217
	.long	623
	.long	3
	.long	399
	.long	853
	.long	407
	.long	103
	.long	983
	.long	89
	.long	463
	.long	290
	.long	516
	.long	212
	.long	462
	.long	350
	.long	960
	.long	376
	.long	682
	.long	962
	.long	300
	.long	780
	.long	486
	.long	502
	.long	912
	.long	800
	.long	250
	.long	346
	.long	172
	.long	812
	.long	350
	.long	870
	.long	456
	.long	192
	.long	162
	.long	593
	.long	473
	.long	915
	.long	45
	.long	989
	.long	873
	.long	823
	.long	965
	.long	425
	.long	329
	.long	803
	.long	973
	.long	965
	.long	905
	.long	919
	.long	133
	.long	673
	.long	665
	.long	235
	.long	509
	.long	613
	.long	673
	.long	815
	.long	165
	.long	992
	.long	326
	.long	322
	.long	148
	.long	972
	.long	962
	.long	286
	.long	255
	.long	941
	.long	541
	.long	265
	.long	323
	.long	925
	.long	281
	.long	601
	.long	95
	.long	973
	.long	445
	.long	721
	.long	11
	.long	525
	.long	473
	.long	65
	.long	511
	.long	164
	.long	138
	.long	672
	.long	18
	.long	428
	.long	154
	.long	448
	.long	848
	.long	414
	.long	456
	.long	310
	.long	312
	.long	798
	.long	104
	.long	566
	.long	520
	.long	302
	.long	248
	.long	694
	.long	976
	.long	430
	.long	392
	.long	198
	.long	184
	.long	829
	.long	373
	.long	181
	.long	631
	.long	101
	.long	969
	.long	613
	.long	840
	.long	740
	.long	778
	.long	458
	.long	284
	.long	760
	.long	390
	.long	821
	.long	461
	.long	843
	.long	513
	.long	17
	.long	901
	.long	711
	.long	993
	.long	293
	.long	157
	.long	274
	.long	94
	.long	192
	.long	156
	.long	574
	.long	34
	.long	124
	.long	4
	.long	878
	.long	450
	.long	476
	.long	712
	.long	914
	.long	838
	.long	669
	.long	875
	.long	299
	.long	823
	.long	329
	.long	699
	.long	815
	.long	559
	.long	813
	.long	459
	.long	522
	.long	788
	.long	168
	.long	586
	.long	966
	.long	232
	.long	308
	.long	833
	.long	251
	.long	631
	.long	107
	.long	813
	.long	883
	.long	451
	.long	509
	.long	615
	.long	77
	.long	281
	.long	613
	.long	459
	.long	205
	.long	380
	.long	274
	.long	302
	.long	35
	.long	805
        .bss
memo:
        .space	2097152

## Local Variables:
## compile-command: "gcc matrix.s && ./a.out"
## End: