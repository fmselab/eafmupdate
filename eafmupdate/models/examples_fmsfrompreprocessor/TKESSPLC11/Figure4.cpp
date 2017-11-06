#include <string>
using namespace std;

class Node {
	string name;
#ifdef Cycle
	bool visited;
#endif
};
class Edge {
#ifdef Undirected
	Node nodeA, nodeB;
#endif
#ifdef Directed
	Node source, target;
#endif
};
#ifdef GraphLibrary
class Graph {
	Node nodes[];
	Edge edges[];
#ifdef Number
	void assignNumbers() {
//assign numbers
	}
#endif
#if defined(Cycle) && defined(Directed)
	bool containsCycle() {
//do cycle checking
		return true;
	}
#endif
};
#endif

int main() {
}

