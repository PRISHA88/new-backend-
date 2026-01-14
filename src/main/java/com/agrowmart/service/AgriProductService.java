//package com.agrowmart.service;
//
//import com.agrowmart.dto.auth.AgriProduct.AgriProductCreateDTO;
//import com.agrowmart.dto.auth.AgriProduct.AgriProductResponseDTO;
//import com.agrowmart.dto.auth.AgriProduct.AgriVendorInfoDTO;
//import com.agrowmart.entity.User;
//import com.agrowmart.entity.AgriProduct.*;
//import com.agrowmart.repository.AgriProductRepository;
//import com.agrowmart.repository.UserRepository;
//import com.agrowmart.service.CloudinaryService; // Make sure this exists
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//
//@Service
//public class AgriProductService {
//
//    @Autowired
//    private AgriProductRepository repository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private CloudinaryService cloudinaryService; // For image upload
//
//    /**
//     * FINAL FIX: Works with your current JwtAuthenticationFilter
//     * Your filter sets the full User entity as principal
//     */
//    private User getCurrentVendor(Authentication auth) {
//        if (auth == null || !auth.isAuthenticated()) {
//            throw new RuntimeException("Unauthorized: No authentication found");
//        }
//
//        Object principal = auth.getPrincipal();
//
//        if (principal instanceof User user) {
//            return user; // ← This is what your filter does — perfect!
//        }
//
//        throw new RuntimeException("Authentication principal is not User entity. Got: " 
//                + (principal == null ? "null" : principal.getClass().getName()));
//    }
//
//    private AgriVendorInfoDTO mapVendor(User user) {
//        return new AgriVendorInfoDTO(
//                user.getId(),
//                user.getName(),
//                user.getPhone(),
//                user.getBusinessName(),
//                user.getPhotoUrl(),
//                user.getCity(),
//                user.getState()
//        );
//    }
//
//    private AgriProductResponseDTO entityToDto(BaseAgriProduct p) {
//        String fertilizerType = null, nutrientComposition = null, fcoNumber = null;
//        String SeedscropType = null, Seedsvariety = null, seedClass = null;
//        Double SeedsgerminationPercentage = null, SeedsphysicalPurityPercentage = null;
//        String SeedslotNumber = null;
//        String Pesticidetype = null, PesticideactiveIngredient = null, Pesticidetoxicity = null;
//        String PesticidecibrcNumber = null, Pesticideformulation = null;
//        String Pipetype = null, Pipesize = null, PipebisNumber = null;
//        Double Pipelength = null;
//
//        if (p instanceof Fertilizer f) {
//            fertilizerType = f.getFertilizerType();
//            nutrientComposition = f.getNutrientComposition();
//            fcoNumber = f.getFcoNumber();
//        } else if (p instanceof Seeds s) {
//            SeedscropType = s.getSeedscropType();
//            Seedsvariety = s.getSeedsvariety();
//            seedClass = s.getSeedClass();
//            SeedsgerminationPercentage = s.getSeedsgerminationPercentage();
//            SeedsphysicalPurityPercentage = s.getSeedsphysicalPurityPercentage();
//            SeedslotNumber = s.getSeedslotNumber();
//        } else if (p instanceof Pesticide pes) {
//            Pesticidetype = pes.getPesticidetype();
//            PesticideactiveIngredient = pes.getPesticideactiveIngredient();
//            Pesticidetoxicity = pes.getPesticidetoxicity();
//            PesticidecibrcNumber = pes.getPesticidecibrcNumber();
//            Pesticideformulation = pes.getPesticideformulation();
//        } else if (p instanceof Pipe pipe) {
//            Pipetype = pipe.getPipetype();
//            Pipesize = pipe.getPipesize();
//            Pipelength = pipe.getPipelength();
//            PipebisNumber = pipe.getPipebisNumber();
//        }
//
//        return new AgriProductResponseDTO(
//                p.getId(),
//                p.getAgriproductName(),
//                p.getAgricategory(),
//                p.getAgridescription(),
//                p.getAgriprice(),
//                p.getAgriunit(),
//                p.getAgriquantity(),
//                p.getAgriimageUrl(),
//                p.getAgribrandName(),
//                p.getAgripackagingType(),
//                p.getAgrilicenseNumber(),
//                p.getAgrilicenseType(),
//                p.getVerified(),
//                mapVendor(p.getVendor()),
//                fertilizerType, nutrientComposition, fcoNumber,
//                SeedscropType, Seedsvariety, seedClass, SeedsgerminationPercentage, SeedsphysicalPurityPercentage, SeedslotNumber,
//                Pesticidetype, PesticideactiveIngredient, Pesticidetoxicity, PesticidecibrcNumber, Pesticideformulation,
//                Pipetype, Pipesize, Pipelength, PipebisNumber
//        );
//    }
//
//    // NEW: Create with image upload support
//    public AgriProductResponseDTO create(
//            AgriProductCreateDTO dto,
//            MultipartFile imageFile,           // Main product image
//            MultipartFile licenseImageFile,    // License document image
//            Authentication auth) {
//
//        User vendor = getCurrentVendor(auth);
//
//        BaseAgriProduct product = switch (dto.category().toUpperCase()) {
//            case "FERTILIZER" -> new Fertilizer();
//            case "SEEDS" -> new Seeds();
//            case "PESTICIDE" -> new Pesticide();
//            case "PIPE" -> new Pipe();
//            default -> throw new IllegalArgumentException("Invalid category: " + dto.category());
//        };
//
//        // Upload images to Cloudinary if provided
//        List imageUrl = dto.AgriimageUrl();
//        if (imageFile != null && !imageFile.isEmpty()) {
//            try {
//				imageUrl = cloudinaryService.upload(imageFile);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//        }
//
//        String licenseImageUrl = dto.AgrilicenseImageUrl();
//        if (licenseImageFile != null && !licenseImageFile.isEmpty()) {
//            try {
//				licenseImageUrl = cloudinaryService.upload(licenseImageFile);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//        }
//
//        // Set common fields
//        product.setAgriproductName(dto.AgriproductName());
//        product.setAgridescription(dto.Agridescription());
//        product.setAgriprice(dto.Agriprice());
//        product.setAgriunit(dto.Agriunit());
//        product.setAgriquantity(dto.Agriquantity());
//        product.setAgriimageUrl(imageUrl);
//        product.setAgribrandName(dto.AgribrandName());
//        product.setAgripackagingType(dto.AgripackagingType());
//        product.setAgrilicenseNumber(dto.AgrilicenseNumber());
//        product.setAgrilicenseType(dto.AgrilicenseType());
//        product.setAgrilicenseImageUrl(licenseImageUrl);
//        product.setAgribatchNumber(dto.AgribatchNumber());
//        product.setAgrimanufacturerName(dto.AgrimanufacturerName());
//        product.setAgrimanufacturingDate(dto.AgrimanufacturingDate());
//        product.setAgriexpiryDate(dto.AgriexpiryDate());
//        product.setVendor(vendor);
//
//        // Set category-specific fields
//        if (product instanceof Fertilizer f) {
//            f.setFertilizerType(dto.fertilizerType());
//            f.setNutrientComposition(dto.nutrientComposition());
//            f.setFcoNumber(dto.fcoNumber());
//        } else if (product instanceof Seeds s) {
//            s.setSeedscropType(dto.SeedscropType());
//            s.setSeedsvariety(dto.Seedsvariety());
//            s.setSeedClass(dto.seedClass());
//            s.setSeedsgerminationPercentage(dto.SeedsgerminationPercentage());
//            s.setSeedsphysicalPurityPercentage(dto.SeedsphysicalPurityPercentage());
//            s.setSeedslotNumber(dto.SeedslotNumber());
//        } else if (product instanceof Pesticide pes) {
//            pes.setPesticidetype(dto.Pesticidetype());
//            pes.setPesticideactiveIngredient(dto.PesticideactiveIngredient());
//            pes.setPesticidetoxicity(dto.Pesticidetoxicity());
//            pes.setPesticidecibrcNumber(dto.PesticidecibrcNumber());
//            pes.setPesticideformulation(dto.Pesticideformulation());
//        } else if (product instanceof Pipe pipe) {
//            pipe.setPipetype(dto.Pipetype());
//            pipe.setPipesize(dto.Pipesize());
//            pipe.setPipelength(dto.Pipelength());
//            pipe.setPipebisNumber(dto.PipebisNumber());
//        }
//
//        return entityToDto(repository.save(product));
//    }
//
//    // Keep other methods unchanged (getAll, getMyProducts, etc.)
//    public List<AgriProductResponseDTO> getAll() {
//        return repository.findAll().stream().map(this::entityToDto).toList();
//    }
//
//    public List<AgriProductResponseDTO> getMyProducts(Authentication auth) {
//        User vendor = getCurrentVendor(auth);
//        return repository.findByVendor(vendor).stream().map(this::entityToDto).toList();
//    }
//
//    public AgriProductResponseDTO getById(Long id) {
//        return entityToDto(repository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Product not found")));
//    }
//
//    // Update method can be enhanced similarly with image upload later
//    public AgriProductResponseDTO update(Long id, AgriProductCreateDTO dto, Authentication auth) {
//        User vendor = getCurrentVendor(auth);
//        BaseAgriProduct existing = repository.findByIdAndVendor(id, vendor)
//                .orElseThrow(() -> new RuntimeException("Product not found or not yours"));
//
//        // Update fields (same as create, without image upload for now)
//        existing.setAgriproductName(dto.AgriproductName());
//        existing.setAgridescription(dto.Agridescription());
//        existing.setAgriprice(dto.Agriprice());
//        existing.setAgriunit(dto.Agriunit());
//        existing.setAgriquantity(dto.Agriquantity());
//        existing.setAgriimageUrl(dto.AgriimageUrl());
//        existing.setAgribrandName(dto.AgribrandName());
//        existing.setAgripackagingType(dto.AgripackagingType());
//        existing.setAgrilicenseNumber(dto.AgrilicenseNumber());
//        existing.setAgrilicenseType(dto.AgrilicenseType());
//        existing.setAgrilicenseImageUrl(dto.AgrilicenseImageUrl());
//        existing.setAgribatchNumber(dto.AgribatchNumber());
//        existing.setAgrimanufacturerName(dto.AgrimanufacturerName());
//        existing.setAgrimanufacturingDate(dto.AgrimanufacturingDate());
//        existing.setAgriexpiryDate(dto.AgriexpiryDate());
//
//        // Category-specific update (same as create)
//        if (existing instanceof Fertilizer f) {
//            f.setFertilizerType(dto.fertilizerType());
//            f.setNutrientComposition(dto.nutrientComposition());
//            f.setFcoNumber(dto.fcoNumber());
//        } else if (existing instanceof Seeds s) {
//            s.setSeedscropType(dto.SeedscropType());
//            s.setSeedsvariety(dto.Seedsvariety());
//            s.setSeedClass(dto.seedClass());
//            s.setSeedsgerminationPercentage(dto.SeedsgerminationPercentage());
//            s.setSeedsphysicalPurityPercentage(dto.SeedsphysicalPurityPercentage());
//            s.setSeedslotNumber(dto.SeedslotNumber());
//        } else if (existing instanceof Pesticide pes) {
//            pes.setPesticidetype(dto.Pesticidetype());
//            pes.setPesticideactiveIngredient(dto.PesticideactiveIngredient());
//            pes.setPesticidetoxicity(dto.Pesticidetoxicity());
//            pes.setPesticidecibrcNumber(dto.PesticidecibrcNumber());
//            pes.setPesticideformulation(dto.Pesticideformulation());
//        } else if (existing instanceof Pipe pipe) {
//            pipe.setPipetype(dto.Pipetype());
//            pipe.setPipesize(dto.Pipesize());
//            pipe.setPipelength(dto.Pipelength());
//            pipe.setPipebisNumber(dto.PipebisNumber());
//        }
//
//        return entityToDto(repository.save(existing));
//    }
//
//    public void delete(Long id, Authentication auth) {
//        User vendor = getCurrentVendor(auth);
//        BaseAgriProduct product = repository.findByIdAndVendor(id, vendor)
//                .orElseThrow(() -> new RuntimeException("Product not found or not yours"));
//        repository.delete(product);
//    }
//
//    public List<AgriProductResponseDTO> search(String keyword) {
//        return repository.search(keyword).stream().map(this::entityToDto).toList();
//    }
//}



package com.agrowmart.service;

import com.agrowmart.dto.auth.AgriProduct.AgriProductCreateDTO;
import com.agrowmart.dto.auth.AgriProduct.AgriProductResponseDTO;
import com.agrowmart.dto.auth.AgriProduct.AgriVendorInfoDTO;
import com.agrowmart.entity.User;
import com.agrowmart.entity.AgriProduct.*;
import com.agrowmart.repository.AgriProductRepository;
import com.agrowmart.repository.UserRepository;
import com.agrowmart.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AgriProductService {

    @Autowired
    private AgriProductRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    private User getCurrentVendor(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Unauthorized: No authentication found");
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof User user) {
            return user;
        }
        throw new RuntimeException("Authentication principal is not User entity. Got: "
                + (principal == null ? "null" : principal.getClass().getName()));
    }

    private AgriVendorInfoDTO mapVendor(User user) {
        return new AgriVendorInfoDTO(
                user.getId(),
                user.getName(),
                user.getPhone(),
                user.getBusinessName(),
                user.getPhotoUrl(),
                user.getCity(),
                user.getState()
        );
    }

    private AgriProductResponseDTO entityToDto(BaseAgriProduct p) {
        // Category-specific fields
        String fertilizerType = null, nutrientComposition = null, fcoNumber = null;
        String SeedscropType = null, Seedsvariety = null, seedClass = null;
        Double SeedsgerminationPercentage = null, SeedsphysicalPurityPercentage = null;
        String SeedslotNumber = null;
        String Pesticidetype = null, PesticideactiveIngredient = null, Pesticidetoxicity = null;
        String PesticidecibrcNumber = null, Pesticideformulation = null;
        String Pipetype = null, Pipesize = null, PipebisNumber = null;
        Double Pipelength = null;

        if (p instanceof Fertilizer f) {
            fertilizerType = f.getFertilizerType();
            nutrientComposition = f.getNutrientComposition();
            fcoNumber = f.getFcoNumber();
        } else if (p instanceof Seeds s) {
            SeedscropType = s.getSeedscropType();
            Seedsvariety = s.getSeedsvariety();
            seedClass = s.getSeedClass();
            SeedsgerminationPercentage = s.getSeedsgerminationPercentage();
            SeedsphysicalPurityPercentage = s.getSeedsphysicalPurityPercentage();
            SeedslotNumber = s.getSeedslotNumber();
        } else if (p instanceof Pesticide pes) {
            Pesticidetype = pes.getPesticidetype();
            PesticideactiveIngredient = pes.getPesticideactiveIngredient();
            Pesticidetoxicity = pes.getPesticidetoxicity();
            PesticidecibrcNumber = pes.getPesticidecibrcNumber();
            Pesticideformulation = pes.getPesticideformulation();
        } else if (p instanceof Pipe pipe) {
            Pipetype = pipe.getPipetype();
            Pipesize = pipe.getPipesize();
            Pipelength = pipe.getPipelength();
            PipebisNumber = pipe.getPipebisNumber();
        }

        return new AgriProductResponseDTO(
                p.getId(),
                p.getAgriproductName(),
                p.getAgricategory(),
                p.getAgridescription(),
                p.getAgriprice(),
                p.getAgriunit(),
                p.getAgriquantity(),
                p.getAgriImageUrls(),  // ← Now returns List<String> from JSON
                p.getAgribrandName(),
                p.getAgripackagingType(),
                p.getAgrilicenseNumber(),
                p.getAgrilicenseType(),
                p.getVerified(),
                mapVendor(p.getVendor()),
                fertilizerType, nutrientComposition, fcoNumber,
                SeedscropType, Seedsvariety, seedClass,
                SeedsgerminationPercentage, SeedsphysicalPurityPercentage, SeedslotNumber,
                Pesticidetype, PesticideactiveIngredient, Pesticidetoxicity,
                PesticidecibrcNumber, Pesticideformulation,
                Pipetype, Pipesize, Pipelength, PipebisNumber
        );
    }

    // ==================================================================
    // CREATE METHOD – Now supports MULTIPLE images + license image
    // ==================================================================
    public AgriProductResponseDTO create(
            AgriProductCreateDTO dto,
            List<MultipartFile> imageFiles,         // Multiple product images
            MultipartFile licenseImageFile,         // Single license image
            Authentication auth) {

        User vendor = getCurrentVendor(auth);

        BaseAgriProduct product = switch (dto.category().toUpperCase()) {
            case "FERTILIZER" -> new Fertilizer();
            case "SEEDS" -> new Seeds();
            case "PESTICIDE" -> new Pesticide();
            case "PIPE" -> new Pipe();
            default -> throw new IllegalArgumentException("Invalid category: " + dto.category());
        };

        // === Upload multiple product images ===
        List<String> uploadedImageUrls = new ArrayList<>();
        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) {
                    try {
                        String url = cloudinaryService.upload(file); // assuming returns String URL
                        uploadedImageUrls.add(url);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to upload product image: " + file.getOriginalFilename(), e);
                    }
                }
            }
        }

        // === Upload license image (if provided) ===
        String licenseImageUrl = dto.AgrilicenseImageUrl(); // fallback to DTO value if any
        if (licenseImageFile != null && !licenseImageFile.isEmpty()) {
            try {
                licenseImageUrl = cloudinaryService.upload(licenseImageFile);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload license image", e);
            }
        }

        // === Set common fields ===
        product.setAgriproductName(dto.AgriproductName());
        product.setAgridescription(dto.Agridescription());
        product.setAgriprice(dto.Agriprice());
        product.setAgriunit(dto.Agriunit());
        product.setAgriquantity(dto.Agriquantity());
        product.setAgriImageUrls(uploadedImageUrls);           // ← Saves as JSON in AgriimageUrl column
        product.setAgribrandName(dto.AgribrandName());
        product.setAgripackagingType(dto.AgripackagingType());
        product.setAgrilicenseNumber(dto.AgrilicenseNumber());
        product.setAgrilicenseType(dto.AgrilicenseType());
        product.setAgrilicenseImageUrl(licenseImageUrl);
        product.setAgribatchNumber(dto.AgribatchNumber());
        product.setAgrimanufacturerName(dto.AgrimanufacturerName());
        product.setAgrimanufacturingDate(dto.AgrimanufacturingDate());
        product.setAgriexpiryDate(dto.AgriexpiryDate());
        product.setVendor(vendor);

        // === Set category-specific fields ===
        if (product instanceof Fertilizer f) {
            f.setFertilizerType(dto.fertilizerType());
            f.setNutrientComposition(dto.nutrientComposition());
            f.setFcoNumber(dto.fcoNumber());
        } else if (product instanceof Seeds s) {
            s.setSeedscropType(dto.SeedscropType());
            s.setSeedsvariety(dto.Seedsvariety());
            s.setSeedClass(dto.seedClass());
            s.setSeedsgerminationPercentage(dto.SeedsgerminationPercentage());
            s.setSeedsphysicalPurityPercentage(dto.SeedsphysicalPurityPercentage());
            s.setSeedslotNumber(dto.SeedslotNumber());
        } else if (product instanceof Pesticide pes) {
            pes.setPesticidetype(dto.Pesticidetype());
            pes.setPesticideactiveIngredient(dto.PesticideactiveIngredient());
            pes.setPesticidetoxicity(dto.Pesticidetoxicity());
            pes.setPesticidecibrcNumber(dto.PesticidecibrcNumber());
            pes.setPesticideformulation(dto.Pesticideformulation());
        } else if (product instanceof Pipe pipe) {
            pipe.setPipetype(dto.Pipetype());
            pipe.setPipesize(dto.Pipesize());
            pipe.setPipelength(dto.Pipelength());
            pipe.setPipebisNumber(dto.PipebisNumber());
        }

        BaseAgriProduct saved = repository.save(product);
        return entityToDto(saved);
    }

    // ==================================================================
    // OTHER METHODS (unchanged except using getAgriImageUrls())
    // ==================================================================
    public List<AgriProductResponseDTO> getAll() {
        return repository.findAll().stream()
                .map(this::entityToDto)
                .toList();
    }

    public List<AgriProductResponseDTO> getMyProducts(Authentication auth) {
        User vendor = getCurrentVendor(auth);
        return repository.findByVendor(vendor).stream()
                .map(this::entityToDto)
                .toList();
    }

    public AgriProductResponseDTO getById(Long id) {
        return entityToDto(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found")));
    }

    // Update can be extended later to support new image uploads
    public AgriProductResponseDTO update(Long id, AgriProductCreateDTO dto, Authentication auth) {
        // ... (your existing update logic – can enhance later with image update)
        // For now, it will just use strings from DTO if needed
        // Existing code remains valid
        User vendor = getCurrentVendor(auth);
        BaseAgriProduct existing = repository.findByIdAndVendor(id, vendor)
                .orElseThrow(() -> new RuntimeException("Product not found or not yours"));

        existing.setAgriproductName(dto.AgriproductName());
        existing.setAgridescription(dto.Agridescription());
        existing.setAgriprice(dto.Agriprice());
        existing.setAgriunit(dto.Agriunit());
        existing.setAgriquantity(dto.Agriquantity());
        existing.setAgribrandName(dto.AgribrandName());
        existing.setAgripackagingType(dto.AgripackagingType());
        existing.setAgrilicenseNumber(dto.AgrilicenseNumber());
        existing.setAgrilicenseType(dto.AgrilicenseType());
        existing.setAgrilicenseImageUrl(dto.AgrilicenseImageUrl());
        existing.setAgribatchNumber(dto.AgribatchNumber());
        existing.setAgrimanufacturerName(dto.AgrimanufacturerName());
        existing.setAgrimanufacturingDate(dto.AgrimanufacturingDate());
        existing.setAgriexpiryDate(dto.AgriexpiryDate());

        // Category-specific fields (same as before)
        if (existing instanceof Fertilizer f) {
            f.setFertilizerType(dto.fertilizerType());
            f.setNutrientComposition(dto.nutrientComposition());
            f.setFcoNumber(dto.fcoNumber());
        } else if (existing instanceof Seeds s) {
            s.setSeedscropType(dto.SeedscropType());
            s.setSeedsvariety(dto.Seedsvariety());
            s.setSeedClass(dto.seedClass());
            s.setSeedsgerminationPercentage(dto.SeedsgerminationPercentage());
            s.setSeedsphysicalPurityPercentage(dto.SeedsphysicalPurityPercentage());
            s.setSeedslotNumber(dto.SeedslotNumber());
        } else if (existing instanceof Pesticide pes) {
            pes.setPesticidetype(dto.Pesticidetype());
            pes.setPesticideactiveIngredient(dto.PesticideactiveIngredient());
            pes.setPesticidetoxicity(dto.Pesticidetoxicity());
            pes.setPesticidecibrcNumber(dto.PesticidecibrcNumber());
            pes.setPesticideformulation(dto.Pesticideformulation());
        } else if (existing instanceof Pipe pipe) {
            pipe.setPipetype(dto.Pipetype());
            pipe.setPipesize(dto.Pipesize());
            pipe.setPipelength(dto.Pipelength());
            pipe.setPipebisNumber(dto.PipebisNumber());
        }

        return entityToDto(repository.save(existing));
    }

    public void delete(Long id, Authentication auth) {
        User vendor = getCurrentVendor(auth);
        BaseAgriProduct product = repository.findByIdAndVendor(id, vendor)
                .orElseThrow(() -> new RuntimeException("Product not found or not yours"));
        repository.delete(product);
    }

    public List<AgriProductResponseDTO> search(String keyword) {
        return repository.search(keyword).stream()
                .map(this::entityToDto)
                .toList();
    }
    
    public AgriProductResponseDTO updateWithImages(
            Long id,
            AgriProductCreateDTO dto,
            List<MultipartFile> newImageFiles,
            MultipartFile newLicenseImage,
            Authentication auth) {

        User vendor = getCurrentVendor(auth);
        BaseAgriProduct existing = repository.findByIdAndVendor(id, vendor)
                .orElseThrow(() -> new RuntimeException("Product not found or not yours"));

  
        
        // === Handle new product images ===
        List<String> currentImages = existing.getAgriImageUrls() != null ? new ArrayList<>(existing.getAgriImageUrls()) : new ArrayList<>();

        if (newImageFiles != null && !newImageFiles.isEmpty()) {
            List<String> uploadedUrls = new ArrayList<>();
            for (MultipartFile file : newImageFiles) {
                if (!file.isEmpty()) {
                    try {
                        String url = cloudinaryService.upload(file);
                        uploadedUrls.add(url);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to upload new product image", e);
                    }
                }
            }
            // Option: Replace all images OR append? Here we REPLACE (common pattern)
            currentImages = uploadedUrls;
        }

        // === Handle new license image ===
        if (newLicenseImage != null && !newLicenseImage.isEmpty()) {
            try {
                String newLicenseUrl = cloudinaryService.upload(newLicenseImage);
                existing.setAgrilicenseImageUrl(newLicenseUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload new license image", e);
            }
        }

        // === Update text fields only if provided (non-null) ===
        if (dto.AgriproductName() != null) existing.setAgriproductName(dto.AgriproductName());
        if (dto.Agridescription() != null) existing.setAgridescription(dto.Agridescription());
        if (dto.Agriprice() != null) existing.setAgriprice(dto.Agriprice());
        if (dto.Agriunit() != null) existing.setAgriunit(dto.Agriunit());
        if (dto.Agriquantity() != null) existing.setAgriquantity(dto.Agriquantity());
        if (dto.AgribrandName() != null) existing.setAgribrandName(dto.AgribrandName());
        if (dto.AgripackagingType() != null) existing.setAgripackagingType(dto.AgripackagingType());
        if (dto.AgrilicenseNumber() != null) existing.setAgrilicenseNumber(dto.AgrilicenseNumber());
        if (dto.AgrilicenseType() != null) existing.setAgrilicenseType(dto.AgrilicenseType());
        if (dto.AgribatchNumber() != null) existing.setAgribatchNumber(dto.AgribatchNumber());
        if (dto.AgrimanufacturerName() != null) existing.setAgrimanufacturerName(dto.AgrimanufacturerName());
        if (dto.AgrimanufacturingDate() != null) existing.setAgrimanufacturingDate(dto.AgrimanufacturingDate());
        if (dto.AgriexpiryDate() != null) existing.setAgriexpiryDate(dto.AgriexpiryDate());

        // Category-specific fields
        if (existing instanceof Fertilizer f) {
            if (dto.fertilizerType() != null) f.setFertilizerType(dto.fertilizerType());
            if (dto.nutrientComposition() != null) f.setNutrientComposition(dto.nutrientComposition());
            if (dto.fcoNumber() != null) f.setFcoNumber(dto.fcoNumber());
        } else if (existing instanceof Seeds s) {
            if (dto.SeedscropType() != null) s.setSeedscropType(dto.SeedscropType());
            if (dto.Seedsvariety() != null) s.setSeedsvariety(dto.Seedsvariety());
            if (dto.seedClass() != null) s.setSeedClass(dto.seedClass());
            if (dto.SeedsgerminationPercentage() != null) s.setSeedsgerminationPercentage(dto.SeedsgerminationPercentage());
            if (dto.SeedsphysicalPurityPercentage() != null) s.setSeedsphysicalPurityPercentage(dto.SeedsphysicalPurityPercentage());
            if (dto.SeedslotNumber() != null) s.setSeedslotNumber(dto.SeedslotNumber());
        } 
        // ... similarly for Pesticide and Pipe

        // Finally set updated image list
        existing.setAgriImageUrls(currentImages);

        BaseAgriProduct saved = repository.save(existing);
        return entityToDto(saved);
    }
}