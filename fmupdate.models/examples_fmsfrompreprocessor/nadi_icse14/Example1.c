/*
 * Example1.c
 *
 *  Created on: 05/set/2014
 *      Author: garganti
 */

#include <stdio.h>

#ifndef Y
void foo() { return; }
#endif

#ifdef X
void bar() { foo (); }
#endif

main() {
}
