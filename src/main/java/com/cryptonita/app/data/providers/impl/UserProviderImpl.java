package com.cryptonita.app.data.providers.impl;

import com.cryptonita.app.data.daos.*;
import com.cryptonita.app.data.entities.*;
import com.cryptonita.app.data.providers.IUserProvider;
import com.cryptonita.app.data.providers.mappers.IMapper;
import com.cryptonita.app.dto.data.request.UserRegisterDTO;
import com.cryptonita.app.dto.data.response.BannedUserResponseDTO;
import com.cryptonita.app.dto.data.response.FavoritesResponseDto;
import com.cryptonita.app.dto.data.response.UserResponseDTO;
import com.cryptonita.app.exceptions.data.BannedUserNotFoundException;
import com.cryptonita.app.exceptions.data.CoinNotFoundException;
import com.cryptonita.app.exceptions.data.FavoritesNotFoundException;
import com.cryptonita.app.exceptions.data.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserProviderImpl implements IUserProvider {

    private static final String COIN_ALREADY_EXISTS = "The coin %s already exists!";
    private static final String USER_ALREADY_EXISTS = "The user already exists!";
    private static final String BANED_USER_ALREADY_EXISTS = "The banned user already exists!";
    private static final String FAVORIES_ALREADY_EXISTS = "The favorites with userName %s and coinName %s already exist";

    private final IUserDao userDao;
    private final IBannedUserDao bannedUserDao;
    private final IAccountDao accountDao;
    private final IFavoritesDao favoritesDao;
    private final ICoinDAO coinDAO;

    private final IMapper<UserModel, UserRegisterDTO> registerDTOIMapper;
    private final IMapper<UserModel, UserResponseDTO> responseDTOIMapper;
    private final IMapper<BannedUsersModel, BannedUserResponseDTO> banResponseDTOIMapper;
    private final IMapper<FavouritesModel, FavoritesResponseDto> favoritesResponseDtoIMapper;

    private final PasswordEncoder encoder;

    @Override
    public UserResponseDTO register(UserRegisterDTO dto) {
        if (userDao.findByMail(dto.mail).isPresent())
            throw new UserNotFoundException(USER_ALREADY_EXISTS);

        if (userDao.findByUsername(dto.username).isPresent())
            throw new UserNotFoundException(USER_ALREADY_EXISTS);

        UserModel user = registerDTOIMapper.mapToEntity(dto);
        user.setPassword(encoder.encode(user.getPassword()));

        user = userDao.save(user);
        user.setAccount(createAccount(user));

        return responseDTOIMapper.mapToDto(user);
    }



    @Override
    public UserResponseDTO getById(long id) {
        return userDao.findById(id)
                .map(responseDTOIMapper::mapToDto)
                .orElseThrow(() -> new UserNotFoundException(USER_ALREADY_EXISTS));
    }

    @Override
    public UserResponseDTO getByName(String name) {
        return userDao.findByUsername(name)
                .map(responseDTOIMapper::mapToDto)
                .orElseThrow(() -> new UserNotFoundException(USER_ALREADY_EXISTS));
    }

    @Override
    public UserResponseDTO getByEmail(String mail) {
        return userDao.findByMail(mail)
                .map(responseDTOIMapper::mapToDto)
                .orElseThrow(() -> new UserNotFoundException(USER_ALREADY_EXISTS));
    }

    @Override
    public boolean matchesPassword(String mail, String password) {
        return innerMatchPassword(userDao.findByMail(mail), password);
    }

    @Override
    public boolean matchesPasswordByUsername(String username, String password) {
        return innerMatchPassword(userDao.findByUsername(username), password);
    }

    /**
     * Inner method to check password
     */
    private boolean innerMatchPassword(Optional<UserModel> user, String rawPassword) {
        return user.isPresent() && encoder.matches(rawPassword, user.get().getPassword());
    }

    @Override
    public boolean exists(String mail) {
        return userDao.findByMail(mail).isPresent();
    }

    @Override
    public boolean existsByUsername(String username) {
        return userDao.findByUsername(username).isPresent();
    }

    @Override
    public BannedUserResponseDTO banUser(String mail) {
        return innerBanUser(userDao.findByMail(mail).orElse(null));
    }

    @Override
    public BannedUserResponseDTO unBanUser(String mail) {
        return innerUnbanUser(bannedUserDao.findByUserMail(mail).orElse(null));
    }

    @Override
    public BannedUserResponseDTO banUserByUsername(String username) {
        return innerBanUser(userDao.findByUsername(username).orElse(null));
    }

    @Override
    public BannedUserResponseDTO unbanUserByUsername(String username) {
        return innerUnbanUser(bannedUserDao.findByUser_Username(username).orElse(null));
    }

    /**
     * Inner ban user method
     */
    private BannedUserResponseDTO innerBanUser(UserModel user) {
        if (user == null)
            throw new UserNotFoundException(USER_ALREADY_EXISTS);

        BannedUsersModel bannedUser = BannedUsersModel.builder()
                .user(user)
                .bannedAt(LocalDate.now())
                .expiresAt(LocalDate.now().plusMonths(1))
                .build();

        bannedUser = bannedUserDao.save(bannedUser);

        return banResponseDTOIMapper.mapToDto(bannedUser);
    }

    /**
     * Inner unban user method
     */
    private BannedUserResponseDTO innerUnbanUser(BannedUsersModel bannedUser) {
        if (bannedUser == null)
            new BannedUserNotFoundException(BANED_USER_ALREADY_EXISTS);

        bannedUserDao.delete(bannedUser);

        return banResponseDTOIMapper.mapToDto(bannedUser);
    }

    @Override
    public List<BannedUserResponseDTO> getBannedUsers() {
        return bannedUserDao.findAll().stream()
                .map(banResponseDTOIMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isBanned(String mail) {
        return bannedUserDao.findByUserMail(mail).isPresent();
    }

    @Override
    public boolean isBannedByUsername(String username) {
        return bannedUserDao.findByUser_Username(username).isPresent();
    }

    @Override
    public BannedUserResponseDTO get(String mail) {
        return bannedUserDao.findByUserMail(mail)
                .map(banResponseDTOIMapper::mapToDto)
                .orElseThrow(() -> new BannedUserNotFoundException(BANED_USER_ALREADY_EXISTS));
    }

    @Override
    public BannedUserResponseDTO getByUsername(String username) {
        return bannedUserDao.findByUser_Username(username)
                .map(banResponseDTOIMapper::mapToDto)
                .orElseThrow(() -> new BannedUserNotFoundException(BANED_USER_ALREADY_EXISTS));
    }

    @Override
    public FavoritesResponseDto addFavourite(String name, String coinStr) {
        UserModel user = userDao.findByUsername(name)
                .orElseThrow(() -> new UserNotFoundException(USER_ALREADY_EXISTS));

        CoinModel coin = coinDAO.findByName(coinStr)
                .orElseThrow(() -> new CoinNotFoundException(String.format(COIN_ALREADY_EXISTS,coinStr)));

        if (favoritesDao.findByUser_UsernameAndCoinName(name, coinStr).isPresent())
            throw new FavoritesNotFoundException(String.format(FAVORIES_ALREADY_EXISTS,name,coinStr));

        FavouritesModel favourite = FavouritesModel.builder()
                .user(user)
                .coin(coin)
                .build();

       favourite = favoritesDao.save(favourite);

       return favoritesResponseDtoIMapper.mapToDto(favourite);
    }

    @Override
    public FavoritesResponseDto removeFavorite(String name, String coinStr) {
        UserModel user = userDao.findByUsername(name)
                .orElseThrow(() -> new UserNotFoundException(USER_ALREADY_EXISTS));

        CoinModel coin = coinDAO.findByName(coinStr)
                .orElseThrow(() -> new CoinNotFoundException(String.format(COIN_ALREADY_EXISTS,coinStr)));

        FavouritesModel favourite = favoritesDao.findByUser_UsernameAndCoinName(name, coinStr).orElse(null);
        if (favourite == null)
            throw new FavoritesNotFoundException(String.format(FAVORIES_ALREADY_EXISTS,name,coinStr));

        favoritesDao.delete(favourite);

        return favoritesResponseDtoIMapper.mapToDto(favourite);
    }

    /**
     * Inner method to create a user account
     */
    private AccountModel createAccount(UserModel user) {
        AccountModel account = AccountModel.builder()
                .user(user)
                .build();

        return accountDao.save(account);
    }


}
