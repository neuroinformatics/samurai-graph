#ifndef __SGPLUGIN_H__
#define __SGPLUGIN_H__

#define TRUE 1
#define FALSE 0

#define SXY 0
#define SXYZ 1
#define SXYZ_GRID 2
#define VXY 3
#define VXY_GRID 4

typedef struct {
	int length;
	int num;
	double** xValues;
	double** yValues;
} SXY_DATA_BUFFER;

typedef struct {
	int length;
	double* xValues;
	double* yValues;
	double* zValues;
} SXYZ_DATA_BUFFER;

typedef struct {
	int xLength;
	int yLength;
	double* xValues;
	double* yValues;
	double** zValues;
} SXYZ_GRID_DATA_BUFFER;

typedef struct {
	int length;
	double* xValues;
	double* yValues;
	double* fValues;
	double* sValues;
	int polar;
} VXY_DATA_BUFFER;

typedef struct {
	int xLength;
	int yLength;
	double* xValues;
	double* yValues;
	double** fValues;
	double** sValues;
	int polar;
} VXY_GRID_DATA_BUFFER;

typedef struct {
	int num;
	int* dataTypes;
	void** dataBuffers;
} DATA_BUFFER_ARRAY;

typedef struct {
	DATA_BUFFER_ARRAY* dataBufferArray;
	int parametersNum;
	char** parameters;
} INPUT_DATA;

typedef struct {
	DATA_BUFFER_ARRAY* dataBufferArray;
	int errmsgNum;
	char** errmsg;
} OUTPUT_DATA;

OUTPUT_DATA* createOutputData();

void setUpOutputData(OUTPUT_DATA* data, int* dataTypes, int dataNum);

void setUpSXYDataBuffer(SXY_DATA_BUFFER* p, int num, int len);

void setUpSXYZDataBuffer(SXYZ_DATA_BUFFER* p, int len);

void setUpSXYZGridDataBuffer(SXYZ_GRID_DATA_BUFFER* p, int xLen, int yLen);

void setUpVXYDataBuffer(VXY_DATA_BUFFER* p, int len);

void setUpVXYGridDataBuffer(VXY_GRID_DATA_BUFFER* p, int xLen, int yLen);

void setErrorMessages(OUTPUT_DATA* data, char** msgArray, int msgNum);

void setErrorMessage(OUTPUT_DATA* data, char* msg);

#endif	/* __SGPLUGIN_H__ */
