typedef int SSH_SESSION;
typedef struct {
	int type;
} PUBKEY;
typedef int SIGNATURE;
#define TYPE_DSS 3
#define TRUE		1

void ssh_set_error(){}

static int sig_verify(SSH_SESSION *session, PUBKEY *pubkey,
		SIGNATURE *signature) {
	// Code here..
	switch (pubkey->type) {
	case TYPE_DSS:
#ifdef HAVE_LIBGCRYPT
		// Code here..
		if (TRUE) { //gcry_err_code (valid) != GPG_ERR_BAD_SIGNATURE){
			ssh_set_error();//ssh_set_error(2, "DSA error : %s", gcry_strerror(valid));
#elif defined (HAVE_LIBCRYPTO)
			// Code here..
			if (TRUE) { //(valid == -1){
				ssh_set_error();//ssh_set_error(session, 2, "DSA error : %s", ERR_get_error());
#endif
		return -1;
	}
	ssh_set_error(); //ssh_set_error(session, 2, "Invalid DSA signature");
	return -1;
	// Other case options
}
return -1;
}

int main() {
}
