#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef char String[30];

void parse_spreadsheet(const char* filename, String** p_arr, int *p_size) {
	int capacity = 32;
	*p_size = 0;
	*p_arr = (String*)malloc(capacity * sizeof(String));
	FILE* fp;
	fopen_s(&fp, filename, "r");
	char line[200];

	while (fgets(line, 200, fp) != NULL) {
		char* next_token;
		char* token = strtok_s(line, "\t\n", &next_token);
		while (token != NULL) {
			if (++(*p_size) > capacity) {
				capacity *= 2;
				String* temp = realloc(*p_arr, capacity * sizeof(String));
				if (*temp == NULL) {
					free(*p_arr);
					exit(1);
				}
				else
					*p_arr = temp;
			}
			strcpy_s((char*) ((*p_arr)[(*p_size) - 1]), 30, token);
			token = strtok_s(NULL, "\t\n", &next_token);
		}
	}
}

void check_lists(String *all_weapons_arr, int all_weapons_size, String* unique_weapons_arr, int unique_weapons_size, String** p_wrong_arr, int* p_wrong_size, int** p_count_arr) {
	*p_count_arr = (int*)calloc(unique_weapons_size, sizeof(int));
	int capacity = 2;
	*p_wrong_arr = (String*)malloc(capacity * sizeof(String));
	*p_wrong_size = 0;
	for (int i = 0; i < all_weapons_size; i++) {
		char* curWeapon = all_weapons_arr[i];
		for (int j = 0; j < unique_weapons_size; j++) {
			if (strcmp(curWeapon, unique_weapons_arr[j]) == 0) {
				(*p_count_arr)[j]++;
				goto weapon_found;
			}
		}
		if (*p_wrong_size >= capacity) {
			capacity *= 2;
			*p_wrong_arr = realloc(*p_wrong_arr, capacity * sizeof(String));
		}
		strcpy_s((*p_wrong_arr)[(*p_wrong_size)++], 30, curWeapon);
	weapon_found:
		continue;
	}
}

int main(void) {
	String* arr; int size;
	parse_spreadsheet("spreadsheet.txt", &arr, &size);
	printf("Found %d weapons\n", size);

	String* weapons_arr; int weapons_size;
	parse_spreadsheet("weapons.txt", &weapons_arr, &weapons_size);
	printf("Found %d unique weaponns\n", weapons_size);

	int* count_arr;
	String* wrong_arr;
	int wrong_size;

	check_lists(arr, size, weapons_arr, weapons_size, &wrong_arr, &wrong_size, &count_arr);

	printf("\nInvalid weapons list (%d found): \n", wrong_size);
	for (int i = 0; i < wrong_size; i++) {
		printf("%s\n", wrong_arr[i]);
	}
	printf("\nWeapon counts list: \n");
	for (int i = 0; i < weapons_size; i++) {
		printf("%s: %d\n", weapons_arr[i], count_arr[i]);
	}

	free(arr);
	free(weapons_arr);
	free(count_arr);
	free(wrong_arr);
}