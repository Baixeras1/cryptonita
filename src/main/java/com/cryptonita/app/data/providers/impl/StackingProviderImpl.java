package com.cryptonita.app.data.providers.impl;

import com.cryptonita.app.data.daos.ICoinDAO;
import com.cryptonita.app.data.daos.IStackingDAO;
import com.cryptonita.app.data.daos.IUserDao;
import com.cryptonita.app.data.entities.CoinModel;
import com.cryptonita.app.data.entities.StackingModel;
import com.cryptonita.app.data.entities.UserModel;
import com.cryptonita.app.data.entities.WalletModel;
import com.cryptonita.app.data.providers.IStackingProvider;
import com.cryptonita.app.data.providers.mappers.IMapper;
import com.cryptonita.app.dto.data.response.StackingDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class StackingProviderImpl implements IStackingProvider {

    private final IMapper<StackingModel, StackingDTO> stackingDTOIMapper;
    private final IStackingDAO stackingDAO;
    private final IUserDao userDao;
    private final ICoinDAO coinDAO;
    /**
     * Este metodo crea un stake
     * @param userName nombre de usuario
     * @param coinName nombre de la moneda de la que se hará el stake
     * @param quantity cantidad de dicha moneda
     * @return el stake creado
     */
    @Transactional
    @Override
    public StackingDTO stake(String userName, String coinName, double quantity,int daysToExpire) {
        UserModel userModel = userDao.findByUsername(userName).orElse(null);
        if(userModel == null)
            throw new RuntimeException("El usuario no existe");
        CoinModel coinModel = coinDAO.findByName(coinName).orElse(null);
        if (coinModel==null)
            throw new RuntimeException("La moneda no existe");


        WalletModel wallet = userModel.getAccount().getWallets().get(coinModel);

        if(wallet == null)
            throw new RuntimeException("No tienes wallet");

        if(wallet.getQuantity() < quantity)
            throw new RuntimeException("Lo siento, no tienes saldo suficiente");


        StackingModel stackingModel = StackingModel.builder()
                .user(userModel)
                .coin(coinModel)
                .createdAt(LocalDateTime.now())
                .quantity(quantity)
                .daysToExpire(daysToExpire)
                .build();


        stackingDAO.save(stackingModel);

        return stackingDTOIMapper.mapToDto(stackingModel);
    }

    /**
     * Este metodo elimina un Stake de un usuario
     * @param userName nombre del usuario
     * @param id del stake que se va a eliminar
     * @return retorna el stake eliminado
     */
    @Override
    public StackingDTO unStake(long id,String userName) {

        UserModel userModel = userDao.findByUsername(userName).orElse(null);
        if(userModel == null)
            throw new RuntimeException("El usuario no existe");
        StackingModel stackingModel = stackingDAO.findById(id).orElse(null);
        if(stackingModel == null)
            throw new RuntimeException("Ese stake no existe");

        StackingModel stackingModelUser = stackingDAO.findByUserUsername(userName).orElse(null);
        if (stackingModelUser == null)
            throw new RuntimeException("Ese usuario no tiene un stake con esa moneda");

        stackingDAO.delete(stackingModel);

        return stackingDTOIMapper.mapToDto(stackingModel);
    }

    /**
     * Este metodo devuelve todos los Stakes de un usuario
     * @param username nombre del usuario
     * @return una lista de stakes de ese usuario
     */
    @Override
    public List<StackingDTO> getAllUserStakes(String username) {
        UserModel userModel = userDao.findByUsername(username).orElse(null);
        if(userModel == null)
            throw new RuntimeException("El usuario no existe");

        List<StackingModel> stackingModelList = stackingDAO.findAllByUserId(userModel.getId());
        List<StackingDTO> stackingDTOList = new ArrayList<>();

        for(StackingModel stackingModel : stackingModelList){
            StackingDTO dto = stackingDTOIMapper.mapToDto(stackingModel);
            stackingDTOList.add(dto);
        }
        return stackingDTOList;
    }

    /**
     * Este metodo te da un stake de un usuario
     * @param id del stake
     * @param username nombre del usuario
     * @return el stake buscado con el id
     */
    @Override
    public StackingDTO getUserStake(long id,String username) {
        UserModel userModel = userDao.findByUsername(username).orElse(null);
        if(userModel == null)
            throw new RuntimeException("El usuario no existe");

        StackingModel stackingModel = stackingDAO.findById(id).orElse(null);
        if (stackingModel == null)
            throw new RuntimeException("No existe ese Stake");

        return stackingDTOIMapper.mapToDto(stackingModel);
    }

    /**
     * Este metodo te da una lista con todos los stakes
     * @return todos los stakes
     */

    @Override
    public List<StackingDTO> findAll() {
        List<StackingModel> stackingModelList = stackingDAO.findAll();
        List<StackingDTO> stackingDTOList = new ArrayList<>();

        for(StackingModel stackingModel : stackingModelList){
            StackingDTO dto = stackingDTOIMapper.mapToDto(stackingModel);
            stackingDTOList.add(dto);
        }
        return stackingDTOList;
    }
}