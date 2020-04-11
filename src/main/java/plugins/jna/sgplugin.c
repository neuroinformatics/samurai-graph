#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "sgplugin.h"

static void freeSXYDataBuffer(SXY_DATA_BUFFER* buffer) {
	int ii;
	if (buffer == NULL) {
		return;
	}
	for (ii = 0; ii < buffer->num; ii++) {
		free(*(buffer->xValues + ii));
		free(*(buffer->yValues + ii));
	}
	free(buffer->xValues);
	free(buffer->yValues);
	free(buffer);
}

static void freeSXYZDataBuffer(SXYZ_DATA_BUFFER* buffer) {
	if (buffer == NULL) {
		return;
	}
	free(buffer->xValues);
	free(buffer->yValues);
	free(buffer->zValues);
	free(buffer);
}

static void freeSXYZGridDataBuffer(SXYZ_GRID_DATA_BUFFER* buffer) {
	int ii;
	if (buffer == NULL) {
		return;
	}
	free(buffer->xValues);
	free(buffer->yValues);
	for (ii = 0; ii < buffer->yLength; ii++) {
		free(*(buffer->zValues + ii));
	}
	free(buffer->zValues);
	free(buffer);
}

static void freeVXYDataBuffer(VXY_DATA_BUFFER* buffer) {
	if (buffer == NULL) {
		return;
	}
	free(buffer->xValues);
	free(buffer->yValues);
	free(buffer->fValues);
	free(buffer->sValues);
	free(buffer);
}

static void freeVXYGridDataBuffer(VXY_GRID_DATA_BUFFER* buffer) {
	int ii;
	if (buffer == NULL) {
		return;
	}
	free(buffer->xValues);
	free(buffer->yValues);
	for (ii = 0; ii < buffer->yLength; ii++) {
		free(*(buffer->fValues + ii));
	}
	free(buffer->fValues);
	for (ii = 0; ii < buffer->yLength; ii++) {
		free(*(buffer->sValues + ii));
	}
	free(buffer->sValues);
	free(buffer);
}

static void freeDataBufferArray(DATA_BUFFER_ARRAY* data) {
	int ii;
	if (data == NULL) {
		return;
	}
	for (ii = 0; ii < data->num; ii++) {
		int dataType = *(data->dataTypes + ii);
		void* p = *(data->dataBuffers + ii);
		if (dataType == SXY) {
			freeSXYDataBuffer((SXY_DATA_BUFFER*) p);
		} else if (dataType == SXYZ) {
			freeSXYZDataBuffer((SXYZ_DATA_BUFFER*) p);
		} else if (dataType == SXYZ_GRID) {
			freeSXYZGridDataBuffer((SXYZ_GRID_DATA_BUFFER*) p);
		} else if (dataType == VXY) {
			freeVXYDataBuffer((VXY_DATA_BUFFER*) p);
		} else if (dataType == VXY_GRID) {
			freeVXYGridDataBuffer((VXY_GRID_DATA_BUFFER*) p);
		}
	}
	free(data->dataTypes);
}

void freeData(OUTPUT_DATA* data) {
	if (data == NULL) {
		return;
	}
	freeDataBufferArray(data->dataBufferArray);
	int ii;
	for (ii = 0; ii < data->errmsgNum; ii++) {
		free(*(data->errmsg + ii));
	}
	free(data->errmsg);
}

OUTPUT_DATA* createOutputData() {
	OUTPUT_DATA* p = (OUTPUT_DATA*) malloc(sizeof(OUTPUT_DATA));
	p->dataBufferArray = (DATA_BUFFER_ARRAY*) malloc(sizeof(DATA_BUFFER_ARRAY));
	p->dataBufferArray->num = 0;
	p->dataBufferArray->dataTypes = NULL;
	p->dataBufferArray->dataBuffers = NULL;
	p->errmsgNum = 0;
	p->errmsg = NULL;
	return p;
}

void setUpOutputData(OUTPUT_DATA* data, int* dataTypes, int dataNum) {
	int ii;
	DATA_BUFFER_ARRAY* p = data->dataBufferArray;
	p->num = dataNum;
	p->dataTypes = (int*) malloc(sizeof(int) * dataNum);
	p->dataBuffers = malloc(sizeof(void*) * dataNum);
	for (ii = 0; ii < dataNum; ii++) {
		int dataType = *(dataTypes + ii);
		void* buffer = NULL;
		if (dataType == SXY) {
			buffer = malloc(sizeof(SXY_DATA_BUFFER));
		} else if (dataType == SXYZ) {
			buffer = malloc(sizeof(SXYZ_DATA_BUFFER));
		} else if (dataType == SXYZ_GRID) {
			buffer = malloc(sizeof(SXYZ_GRID_DATA_BUFFER));
		} else if (dataType == VXY) {
			buffer = malloc(sizeof(VXY_DATA_BUFFER));
		} else if (dataType == VXY_GRID) {
			buffer = malloc(sizeof(VXY_GRID_DATA_BUFFER));
		}
		*(p->dataTypes + ii) = dataType;
		*(p->dataBuffers + ii) = buffer;
	}
}

void setUpSXYDataBuffer(SXY_DATA_BUFFER* p, int num, int len) {
	int ii;
	p->num = num;
	p->length = len;
	double** px = (double**) malloc(sizeof(double*) * num);
	double** py = (double**) malloc(sizeof(double*) * num);
	for (ii = 0; ii < num; ii++) {
		*(px + ii) = (double*) malloc(sizeof(double) * len);
		*(py + ii) = (double*) malloc(sizeof(double) * len);
	}
	p->xValues = px;
	p->yValues = py;
}

void setUpSXYZDataBuffer(SXYZ_DATA_BUFFER* p, int len) {
	p->length = len;
	p->xValues = (double*) malloc(sizeof(double) * len);
	p->yValues = (double*) malloc(sizeof(double) * len);
	p->zValues = (double*) malloc(sizeof(double) * len);
}

void setUpSXYZGridDataBuffer(SXYZ_GRID_DATA_BUFFER* p, int xLen, int yLen) {
	int ii;
	p->xLength = xLen;
	p->yLength = yLen;
	p->xValues = (double*) malloc(sizeof(double) * xLen);
	p->yValues = (double*) malloc(sizeof(double) * yLen);
	p->zValues = (double**) malloc(sizeof(double*) * yLen);
	for (ii = 0; ii < yLen; ii++) {
		*(p->zValues + ii) = (double*) malloc(sizeof(double) * xLen);
	}
}

void setUpVXYDataBuffer(VXY_DATA_BUFFER* p, int len) {
	p->length = len;
	p->xValues = (double*) malloc(sizeof(double) * len);
	p->yValues = (double*) malloc(sizeof(double) * len);
	p->fValues = (double*) malloc(sizeof(double) * len);
	p->sValues = (double*) malloc(sizeof(double) * len);
}

void setUpVXYGridDataBuffer(VXY_GRID_DATA_BUFFER* p, int xLen, int yLen) {
	int ii;
	p->xLength = xLen;
	p->yLength = yLen;
	p->xValues = (double*) malloc(sizeof(double) * xLen);
	p->yValues = (double*) malloc(sizeof(double) * yLen);
	p->fValues = (double**) malloc(sizeof(double*) * yLen);
	p->sValues = (double**) malloc(sizeof(double*) * yLen);
	for (ii = 0; ii < yLen; ii++) {
		*(p->fValues + ii) = (double*) malloc(sizeof(double) * xLen);
		*(p->sValues + ii) = (double*) malloc(sizeof(double) * xLen);
	}
}

void setErrorMessages(OUTPUT_DATA* data, char* msgArray[], int msgNum) {
	if (data == NULL) {
		return;
	}
	data->errmsgNum = msgNum;
	if (msgNum == 0) {
		data->errmsg = NULL;
	} else {
		data->errmsg = (char**) malloc(sizeof(char*) * msgNum);
		int ii;
		for (ii = 0; ii < msgNum; ii++) {
			int len = strlen(*(msgArray + ii)) + 1;
			*(data->errmsg + ii) = (char*) malloc(sizeof(char) * len);
			strcpy(*(data->errmsg + ii), *(msgArray + ii));
		}
	}
}

void setErrorMessage(OUTPUT_DATA* data, char* msg) {
	char* p[] = { msg };
	setErrorMessages(data, p, 1);
}

