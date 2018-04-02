cmake_minimum_required(VERSION 3.6)
set(CMAKE_VERBOSE_MAKEFILE on)
set(lib_src_DIR ${CMAKE_CURRENT_SOURCE_DIR})
set(lib_build_DIR $ENV{HOME}/tmp)
file(MAKE_DIRECTORY ${lib_build_DIR})
add_library(p6300uart SHARED reader.c)

