cmake_minimum_required(VERSION 3.22)
project("audioeq_dsp")

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -ffast-math -mfpu=neon -mfloat-abi=softfp -flto=auto")

# Include Oboe from source
include(FetchContent)
FetchContent_Declare(
    oboe
    GIT_REPOSITORY https://github.com/google/oboe.git
    GIT_TAG        1.9.0
    GIT_SHALLOW    TRUE
)
set(oboe_BUILD_TESTS OFF CACHE BOOL "Build Oboe tests" FORCE)
FetchContent_MakeAvailable(oboe)

# Collect DSP sources (relative to this CMakeLists.txt location)
file(GLOB DSP_SOURCES
    "*.cpp"
    "*.h"
)

# Build shared library
add_library(audioeq_dsp SHARED ${DSP_SOURCES})

target_include_directories(audioeq_dsp PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}
    ${oboe_SOURCE_DIR}/include
)

target_link_libraries(audioeq_dsp
    oboe
    log
    OpenSLES
)
