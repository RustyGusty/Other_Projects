#include <string.h>
#include <stdio.h>
#include <stdlib.h>

typedef struct transact {
	char date[11];
	float cost;
} transact;


int main(void) {
	FILE* fp_r = fopen("finances.txt", "r");
	FILE* fp_w = fopen("sheets.txt", "w");

	char line[500]; int count = 0; char* token;
	while (fgets(line, sizeof(line), fp_r)) {
		count++;
	}
	transact* arr = (transact*)malloc(count * sizeof(transact));

	rewind(fp_r);
	for (int i = 0; i < count; i++) {
		fgets(line, sizeof(line), fp_r);
		token = strtok(line, " \t");
		strcpy(arr[i].date, token);
		while (strcmp(token, "Debit") != 0) {
			token = strtok(NULL, " \t");
		}
		token = strtok(NULL, " \t");
		arr[i].cost = atof(token + 1);
	}
	for (int i = count - 1; i >= 0; i--) {
		fprintf(fp_w, "%s\t%f\n", arr[i].date, arr[i].cost);
	}
	free(arr);
	fclose(fp_r);
	fclose(fp_w);



}