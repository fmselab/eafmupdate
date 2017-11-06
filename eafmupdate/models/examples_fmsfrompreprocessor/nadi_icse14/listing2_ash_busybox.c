#ifdef ASH //represents the file presence condition
#ifdef NOMMU
#error "... ash will not run on NOMMU machine"
#endif

#ifdef EDITINGvoid initEditing(){}typedef struct { int flags;} line_input_t;
static line_input_t *line_input_state;

void init() {
	initEditing();
	int maxlength = 1
#ifdef MAX_LEN
	* 100
#endif	;}
#endif //EDITING

int main() {
#ifdef EDITING_VI
#ifdef MAX_LEN
	line_input_state->flags |= 100;
#endif
#endif
}
#endif //ASH
