#define _CRT_SECURE_NO_WARNINGS

#include <iostream>
#include <fstream>
#include <sstream>
#include <stdlib.h>
#include <cstdlib>
#include <cstdio>
#include <string>
#include <utility>
#include <vector>
#include <stack>
#include <limits>
#include <string.h>

#define header_size 12
#define pageid_offet(BID, block_size) (header_size + (BID - 1) * block_size)
#define node_entries(block_size) (block_size - 4) / 8

using namespace std;

class Entry {
private:
	int key;
	int value;
	int fill;

public:
	Entry(int k = 0, int v = 0, int f = 0) {
		key = k;
		value = v;
		fill = f;
	}

	friend class Node;
	friend class Btree;
};

class Node {
private:
	vector<Entry> vec_entry;
	int next_bid;
	int B;
	int max_entry;
	int count;

public:
	Node(int next_bid = -1, int b = -1) {
		Entry empty_entry;

		this->next_bid = next_bid;
		this->B = b;
		this->max_entry = b - 1;
		this->count = 0;

		vec_entry.assign(0, empty_entry);
	}

	void insert_entry(int i, Entry entry) {
		if (is_fill(i) == false) {
			vec_entry[i] = entry;
			vec_entry[i].fill = 1;
			count++;
		}
		else if (is_fill(i) == true) {
			for (int j = i; j <= B; j++) {
				if (vec_entry[i].fill == 1) {
					vec_entry[j + 1] = vec_entry[j];
				}
				vec_entry[i] = entry;
				count++;
			}
		}
	}

	bool is_fill(int i) {
		if (vec_entry[i].fill == 1) {
			return true;
		}
		else {
			return false;
		}

	}

	friend class Entry;
	friend class Btree;
};

class Btree {
private:
	int block_size;
	int root_bid;
	int last_bid;
	int depth;
	int B;
	int max_entry;
	//int length;
	fstream file;
	//FILE* pFile;
	//vector<Node*> root_to_leaf;
	Node* root;
	char* file_name;

public:
	Btree(const char* file_name) {
		file.open(file_name, ios::in | ios::out | ios::binary);
		//this->length = ftell(pFile);
		//fread(reinterpret_cast<char*>(&block_size), sizeof(int), length, pFile);
		//fread(reinterpret_cast<char*>(&root_bid), sizeof(int), length, pFile);
		//fread(reinterpret_cast<char*>(&depth), sizeof(int), length, pFile);
		file.read(reinterpret_cast<char*>(&block_size), sizeof(int));
		file.read(reinterpret_cast<char*>(&root_bid), sizeof(int));
		file.read(reinterpret_cast<char*>(&depth), sizeof(int));

		last_bid = ((int)(file.tellg()) - header_size) / block_size + 1;
		B = node_entries(block_size) + 1;
		root = new Node(block_size);
		max_entry = B - 1;

		strcpy(this->file_name, file_name);

		//fclose(pFile);
		file.close();
	}
	//FILE I/O 중 O
	Node* read_block(int bid) {
		int* stream = new int[block_size / sizeof(int)];
		int position = (bid - 1) * block_size + header_size;

		//pFile = fopen(file_name, "wb");
		//int length = ftell(pFile);
		file.open(file_name, ios::in | ios::out | ios::binary);

		//fseek(pFile, position, SEEK_CUR);
		//fread(reinterpret_cast<char*>(&stream), sizeof(int), length, pFile);
		//fclose(pFile);
		file.seekg(position);
		file.read(reinterpret_cast<char*>(stream), block_size);
		file.close();

		int b = node_entries(block_size) + 1;

		Node* node = new Node(stream[0], b);

		for (int i = 1; i < b; i++) {
			node->vec_entry[i].key = stream[i * 2 - 1];
			node->vec_entry[i].value = stream[i * 2];
		}

		return node;
	}

	void insert(int key, int rid) {
		Node* current_node;
		int current_bid;

		Entry init_entry(key, rid);
		
		current_node = read_block(current_bid);

		for (int i = 1; i <= B; i++) {
			if (i != depth) { //leaf
				
				if (key < current_node->vec_entry[i].key) {
					if (!current_node->is_fill(i)) {
						init_entry.fill = 1;
						current_node->vec_entry[i] = init_entry;
					}
					else {
						for (int j = 1; j <= B; j++) {
							current_node->vec_entry[j + 1] = current_node->vec_entry[j];
						}
						init_entry.fill = 1;
						current_node->vec_entry[i] = init_entry;
					}
				}

				int* stream = new int[block_size / sizeof(int)];
				int position = (current_bid - 1) * block_size + header_size;

				//pFile = fopen(file_name, "wb");
				//int length = ftell(pFile);
				file.seekp(position);
				file.write(reinterpret_cast<char*>(stream), block_size);
				//pFile = fopen(file_name, "wb");
				file.close();
			}
		}

		if (current_node->is_fill(B) == true) {
			Node* new_node = new Node(current_node->next_bid, B);
			int new_bid = last_bid + 1;
			last_bid++;

			for (int i = B / 2 + 1; i <= B; i++) {
				int j = i - B / 2;

				new_node->vec_entry[j] = current_node->vec_entry[i];
				
				current_node->vec_entry[i] = init_entry;
				current_node->vec_entry[i].fill = 1;
			}

			current_node->next_bid = new_node->next_bid;

			int* stream = new int[block_size / sizeof(int)];
			int position = (current_bid - 1) * block_size + header_size;

			file.seekp(position);
			file.write(reinterpret_cast<char*>(stream), block_size);
			file.close();
		}

		
		Node* parent_node;
		int parent_bid;


		Entry entry(key, rid);
		Entry empty_entry(0, 0);

	}

	int search(int key) { //point search
		Node* current_node;
		int next_bid = root_bid;

		for (int i = 1; i <= depth; i++) {
			if (i == depth) {
				current_node = read_block(next_bid);

				for (int j = 1; j <= B; j++) {
					if (key == current_node->vec_entry[j].key) {
						return current_node->vec_entry[j].value;
					}
				}
			}
		}
		
	}

	int* search(int startRange, int endRange) { //range search
		Node* current_node;
		int next_bid = root_bid;
		

	}

	void print() {
		ofstream oFile(file_name, ios::out | ios::trunc);
		Node* current_node = read_block(root_bid);
		vector<int> vec_bid;

		oFile << "<0>" << endl;
		for (int i = 1; i <= B; i++) {
			if (current_node->vec_entry[i].fill == 0) {
				break;
			}
			else {
				oFile << "," << current_node->vec_entry[i].key;
			}
		}
		oFile << endl;

		oFile.close();
	}
};

int main(int argc, const char* argv[]) {
	char command = argv[1][0];

	switch (command) {
	case 'c': {
		//FILE* pFile = fopen (argv[2], "wb");
		ofstream cfile(argv[2], ios::trunc | ios::binary);

		if (cfile.is_open()) {
			int block_size = atoi(argv[3]);
			int B = node_entries(block_size) + 1;
			int root_bid = 1;
			int depth = 1;

			//fwrite(reinterpret_cast<char*>(&block_size), sizeof(int), length, pFile);
			//fwrite(reinterpret_cast<char*>(&root_bid), sizeof(int), length, pFile);
			//fwrite(reinterpret_cast<char*>(&depth), sizeof(int), length, pFile);

			//여기까지 FILE* 구조로 하다가 creation 실행이 계속 안돼서 마지막에 fstream구조로 바꿈

			cfile.write(reinterpret_cast<char*>(&block_size), sizeof(int));
			cfile.write(reinterpret_cast<char*>(&root_bid), sizeof(int));
			cfile.write(reinterpret_cast<char*>(&depth), sizeof(int));
		}

		cfile.close();

		break;
	}

	case 'i': {
		Btree* myBtree = new Btree(argv[2]);
		string line;
		char* buffer;

		ifstream iFile(argv[3], ios::in);

		if (iFile.is_open()) {
			while (getline(iFile, line)) {
				stringstream file_line(line);
				string key;
				string value;

				getline(file_line, key, ',');
				getline(file_line, value, ',');

				int k = atoi(key.c_str());
				int v = atoi(value.c_str());

				myBtree->insert(k , v);
			}
		}

		break;
	}

	case 's': {
		Btree* myBtree = new Btree(argv[2]);
		int key;
		ifstream iFile(argv[3], ios::in);
		ofstream oFile(argv[4], ios::out | ios::trunc);

		if (iFile.is_open()) {
			int key;

			while (iFile >> key) {
				oFile << key << " , " << myBtree->search(key) << endl;
			}
		}

		iFile.close();
		oFile.close();

		break;
	}

	case 'r': {
		Btree* myBtree = new Btree(argv[2]);
		ifstream iFile(argv[3], ios::in);
		ofstream oFile(argv[4], ios::out | ios::trunc);
		string line;
		
		if (iFile.is_open()) {
			while (getline(iFile, line)) {
				stringstream file_line(line);
				string start_range;
				string end_range;

				getline(file_line, start_range, ',');
				getline(file_line, end_range, ',');

				int s = atoi(start_range.c_str());
				int e = atoi(end_range.c_str());

				int* output = myBtree->search(s, e);
				int length = output[0] + 1;

				for (int i = 0; i < length; i++) {
					oFile << output[i] << ", " << output[i + 1] << " ";
				}

				oFile << endl;
			}
		}

		break;
	}

	case 'p' : {
		Btree* myBtree = new Btree(argv[2]);
		const char* file_name = argv[3];

		myBtree->print();

		break;
	}
	
	default: {
		cout << "chose one from [c], [i], [s], [r], [p]" << endl;
	}

	}

	return 0;
}