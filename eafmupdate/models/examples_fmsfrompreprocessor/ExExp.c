#include <stdio.h>

main() {
    int x, y =0;
    if (x>0
#ifdef Y_POS
    		|| y>=0
#endif
    		)
	printf("CIAO");
}
