/*
 * Example1.c
 *
 *  Created on: 05/set/2014
 *      Author: garganti
 */

#include <stdlib.h>

#ifdef BIGINT
#define SIZE 64
#else
#define SIZE 32
#endif

main() {
	int *ptr = 	malloc(SIZE);
}
