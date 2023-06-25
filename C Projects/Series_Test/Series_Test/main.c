#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Sum of 1/(n(1 + 1/2 + 1/3 + ... + 1/n))
void series(unsigned long long int n, long double* cur_sum, long double* cur_term) {
	*cur_term += 1 / (long double) n;
	*cur_sum += 1 / (n * *cur_term);
}

// Sum of 1/(n(1 + 1/4 + 1/9 + ... + 1/n^2))
void basel(unsigned long long int n, long double* cur_sum, long double* cur_term) {
	*cur_term = 1 / (long double)(n * n);
	*cur_sum += *cur_term;
}

int main(void) {
	FILE* fp;
	fopen_s(&fp, "basel.txt", "a");
	long double cur_sum = 0;
	long double cur_term = 0;
	unsigned long long int n = UINT_MAX;
	unsigned long long int cur_pow2 = 1;
	for (unsigned long long int i = 1; i < n; i++) {
		basel(i, &cur_sum, &cur_term);
		if (i == cur_pow2) {
			printf("i = %llu\n", i);
			fprintf_s(fp, "%llu: %.53Lf, %.53Lf\n", i, cur_sum, cur_term);
			cur_pow2 *= 2;
		}
	}
	fprintf_s(fp, "%llu: %.53Lf, %.53Lf\n", n, cur_sum, cur_term);
	fclose(fp);

}