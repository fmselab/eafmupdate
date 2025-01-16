#include <stdio.h>

#ifdef WORLD
char* msg = "Hello World\n";
#endif
#ifdef BYE
char* msg = "Bye bye!\n";
#endif

main() {
	printf(msg);
}
