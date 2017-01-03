
rm -rf *.o
rm -rf cambriaSamplePost
rm -rf cambriaSampleFetch
rm -rf loopPost

#-DCAMBRIA_TRACING

g++ cambria.cpp samplePostClient.cpp -o cambriaSamplePost
g++ cambria.cpp sampleGetClient.cpp -o cambriaSampleFetch 

g++ cambria.cpp loopingPostClient.cpp -o loopPost

