const int NA = -1;

enum NodeType {
	ROOT,
	INTERNAL,
	LEAF
}

class RepeatFinder {
	public static void main(String args[]) {
		String dna;
		Node root;

		// todo: get dna string from FASTA file

		GenerateSuffixTree(dna);
	}

	public static Node GenerateSuffixTree(String str) {
		// Create first tree with root and 1 node whose label is entire string
		Node root = new Node(NodeType.ROOT, NA, NA, NA); //type, id, index, length
		switch(str[0])
		{
			case 'A':
				break;
			case 'T':
				break;
			case 'G':
				break;
			case 'C':
				break;
			default:
				// N or anything else
				break;
		}
		
		// Use recursion to complete the suffix tree
	}

	public static Node ExtendSuffixTree(Node root)
	{

	}
}

class Node {
	NodeType type;
	int id;
	int labelIndex;
	int labelLength;
	Node A, T, G, C, N;
	
	public Node()
	{
		this.type = NodeType.INTERNAL;
		this.id = NA;
		this.labelIndex = NA;
		this.labelLength = NA;
		this.A = this.T = this.G = this.C = this.N = null;
	}

	public Node(NodeType t, int id, int i, int l) {
		this.type = t;
		this.id = id;
		this.labelIndex = i;
		this.labelLength = l;
		this.A = this.T = this.G = this.C = this.N = null;
	}

}
