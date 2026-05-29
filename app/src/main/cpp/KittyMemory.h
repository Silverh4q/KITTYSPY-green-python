#ifndef KITTY_MEMORY_H
#define KITTY_MEMORY_H

#include <string>
#include <vector>
#include <stdint.h>
#include <sys/types.h>

namespace KittyMemory {
    // Representation of a mapped memory region in /proc/self/maps
    struct MemoryRegion {
        uintptr_t startAddress;
        uintptr_t endAddress;
        std::string permissions;
        std::string name;
    };

    // Parse /proc/self/maps to locate libraries and memory layout
    std::vector<MemoryRegion> getMemoryMaps();

    // Find base address of a loaded shared library (.so)
    uintptr_t getLibraryBaseAddress(const std::string& libName);

    // Patch virtual memory with raw bytes
    bool patchMemory(uintptr_t address, const std::vector<uint8_t>& patchBytes);

    // Initialize mock patches for virtual environment
    std::string simulatePatch(const std::string& packageName, uintptr_t targetOffset, const std::string& hexBytes);
}

#endif // KITTY_MEMORY_H
