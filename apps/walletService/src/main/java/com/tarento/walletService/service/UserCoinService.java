package com.tarento.walletService.service;

import com.tarento.walletService.constants.WalletServiceConstants;
import com.tarento.walletService.exception.BadRequestException;
import com.tarento.walletService.repository.UserWalletRepo;
import com.tarento.walletService.repository.WalletInfoRepo;
import com.tarento.walletService.web.models.UserWallet;
import com.tarento.walletService.web.models.WalletInfo;
import com.tarento.walletService.web.models.communication.UserCoin;
import com.tarento.walletService.web.models.communication.UserCoinRequestInfo;
import com.tarento.walletService.web.models.communication.UserCoinResponseInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserCoinService {

    Logger logger = LogManager.getLogger(UserCoinService.class);

    @Autowired
    private UserWalletRepo userWalletRepo;

    @Autowired
    private WalletInfoRepo walletInfoRepo;

    /**
     * @param wid                 wid of the login user
     * @param userCoinRequestInfo wids for users
     * @return List of users coins info with expiry date
     */
    public List<UserCoinResponseInfo> getUsersCoins(String wid, UserCoinRequestInfo userCoinRequestInfo) throws Exception {
        if (StringUtils.isEmpty(wid))
            throw new BadRequestException("invalid.wid :  wid is required for login user");
        List<Map<String, String>> widMap = userCoinRequestInfo.getUserCoinReq();
        List<String> wids = new ArrayList<>();
        Map<String, Integer> widAndStatusCode = new HashMap<>();
        widMap.forEach(map -> {
            if (!StringUtils.isEmpty(map.getOrDefault(WalletServiceConstants.WID, null))) {
                wids.add(map.get(WalletServiceConstants.WID));
            }
        });
        List<UserWallet> userWallets = userWalletRepo.findByWids(wids);
        List<WalletInfo> walletRecords = walletInfoRepo.getUserCoins(userWallets.stream().map(userWallet -> userWallet.getId()).collect(Collectors.toList()));
        return wrapTheResponse(wids, walletRecords);
    }

    /**
     * @param wids
     * @param walletRecords
     * @return List of User Coins
     */
    private List<UserCoinResponseInfo> wrapTheResponse(List<String> wids, List<WalletInfo> walletRecords) {
        HashMap<String, List<UserCoin>> responseInfos = new HashMap<>();
        for (WalletInfo walletInfo : walletRecords) {
            if (!StringUtils.isEmpty(responseInfos.getOrDefault(walletInfo.getUserWalletInfo().getWid(), null))) {
                UserCoin userCoin = new UserCoin();
                userCoin.setNumberOfCoins(walletInfo.getNumberOfCoins());
                userCoin.setExpiryDate(walletInfo.getExpiryDate());
                responseInfos.get(walletInfo.getUserWalletInfo().getWid()).add(userCoin);
            } else {
                UserCoin userCoin = new UserCoin();
                userCoin.setNumberOfCoins(walletInfo.getNumberOfCoins());
                userCoin.setExpiryDate(walletInfo.getExpiryDate());
                List<UserCoin> coins = Arrays.asList(userCoin);
                responseInfos.put(walletInfo.getUserWalletInfo().getWid(), coins);
            }
        }
        List<UserCoinResponseInfo> responseInfoList = new ArrayList<>();
        for (String id : wids) {
            UserCoinResponseInfo userCoinResponseInfo = new UserCoinResponseInfo();
            if (!StringUtils.isEmpty(responseInfos.getOrDefault(id, null))) {
                userCoinResponseInfo.setWid(id);
                userCoinResponseInfo.setResponseCode(WalletServiceConstants.SUCCESS_STATUS_CODE);
                userCoinResponseInfo.setResponseData(responseInfos.get(id));
                responseInfoList.add(userCoinResponseInfo);
            } else {
                userCoinResponseInfo.setWid(id);
                userCoinResponseInfo.setResponseCode(WalletServiceConstants.FAILED_STATUS_CODE);
                Map<String, Object> errorMap = new HashMap<>();
                errorMap.put(WalletServiceConstants.ERROR_CODE_CONST, WalletServiceConstants.FAILED_STATUS_CODE);
                errorMap.put(WalletServiceConstants.ERROR_MESSAGE_CONST, WalletServiceConstants.ERROR_MESSAGE_FOR_WID);
                userCoinResponseInfo.setResponseData(errorMap);
                responseInfoList.add(userCoinResponseInfo);
            }
        }
        return responseInfoList;
    }

}
