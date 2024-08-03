package gift.Service;

import gift.DTO.WishDTO;
import gift.Entity.UserEntity;
import gift.Entity.WishEntity;
import gift.Mapper.WishServiceMapper;
import gift.Repository.UserRepository;
import gift.Repository.WishRepository;
import gift.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WishService {


    private WishRepository wishRepository;
    private WishServiceMapper wishServiceMapper;
    private JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;

    @Autowired
    public WishService(WishRepository wishRepository, UserRepository userRepository, JwtTokenUtil jwtTokenUtil) {
        this.wishRepository = wishRepository;
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public List<WishDTO> findAllWishes() {
        List<WishEntity> wishEntities = wishRepository.findAll();
        return wishServiceMapper.convertToWishDTOs(wishEntities);
    }

    public Optional<WishDTO> findWishById(Long id) {
        Optional<WishEntity> wishEntity = wishRepository.findById(id);
        return wishEntity.map(wishServiceMapper::convertToDTO);
    }

    public WishDTO saveWish(WishDTO wishDTO) {
        WishEntity wishEntity = wishServiceMapper.convertToEntity(wishDTO);
        WishEntity savedWishEntity = wishRepository.save(wishEntity);
        return wishServiceMapper.convertToDTO(savedWishEntity);
    }

    public WishDTO updateWish(Long id, WishDTO wishDTO) {
        Optional<WishEntity> existingWish = wishRepository.findById(id);
        if (existingWish.isPresent()) {
            WishEntity wish = existingWish.get();
            wish.setProduct(wishServiceMapper.convertToProductEntity(wishDTO.getProductId()));
            wish.setUser(wishServiceMapper.convertToUserEntity(wishDTO.getUserId()));
            WishEntity updatedWishEntity = wishRepository.save(wish);
            return wishServiceMapper.convertToDTO(updatedWishEntity);
        } else {
            throw new RuntimeException("Wish not found");
        }
    }

    public void deleteWish(Long id) {
        wishRepository.deleteById(id);
    }

    public Page<WishDTO> getUserWishes(String token, Pageable pageable) {
        String email = jwtTokenUtil.extractUsername(token.substring(7)); // Bearer 제거
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        Page<WishEntity> wishEntities = wishRepository.findByUser(user, pageable);

        return wishEntities.map(wish -> new WishDTO(
                wish.getId(),
                wish.getUser().getId(),
                wish.getProduct().getId(),
                wish.getProduct().getName(),
                wish.getProduct().getPrice(),
                wish.getProduct().getImageUrl()
        ));
    }
}
