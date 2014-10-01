# Makefile for loglet1g.c  (good luck :-)
# -w inhibits all warning messages

CC = gcc
CFLAGS = 

lmg : loglet1g.o main_loglet_fitting.o log_f1.o nlogis.o nrutil.o covsrt.o mrqmin.o mrqcof.o gaussj.o logfunc.o ran1.o moment.o
	$(CC) $(CFLAGS) -o lmg loglet1g.c main_loglet_fitting.c log_f1.c nlogis.c logfunc.c nrutil.c covsrt.c mrqmin.c mrqcof.c gaussj.c ran1.c moment.c -lm
