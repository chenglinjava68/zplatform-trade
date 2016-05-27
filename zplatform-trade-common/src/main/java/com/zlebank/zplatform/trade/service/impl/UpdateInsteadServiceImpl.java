/* 
haod	支付			
姓名	郭佳			
日期	2016年5月23日			
				
当日工作计划				
序号	时长(时)	工作描述	优先级	备注
1	7	集成畅捷支付代付代码	高	
				
当日工作总结				
序号	时长(时)	工作描述	优先级	备注
1	7.5	开发退款功能，测试退款，修改生成退款订单的方法，修改测试中出现的bug	中	
				
				
问题情况				
序号	问题描述		优先级	备注
				
次日工作计划				
序号	时长(时)	工作描述	优先级	备注
1	8	集成畅捷支付代付代码编写各个接口的单元测试程序	高	
 * UpdateInsteadServiceImpl.java  
 * 
 * version TODO
 *
 * 2016年3月16日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.trade.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.zlebank.zplatform.acc.bean.TradeInfo;
import com.zlebank.zplatform.acc.exception.AbstractBusiAcctException;
import com.zlebank.zplatform.acc.exception.AccBussinessException;
import com.zlebank.zplatform.acc.service.AccEntryService;
import com.zlebank.zplatform.acc.service.entry.EntryEvent;
import com.zlebank.zplatform.commons.dao.pojo.AccStatusEnum;
import com.zlebank.zplatform.member.dao.CoopInstiDAO;
import com.zlebank.zplatform.member.pojo.PojoCoopInsti;
import com.zlebank.zplatform.member.pojo.PojoMember;
import com.zlebank.zplatform.member.service.MemberService;
import com.zlebank.zplatform.trade.bean.UpdateData;
import com.zlebank.zplatform.trade.bean.enums.InsteadPayDetailStatusEnum;
import com.zlebank.zplatform.trade.bean.enums.TransferBusiTypeEnum;
import com.zlebank.zplatform.trade.dao.InsteadPayBatchDAO;
import com.zlebank.zplatform.trade.dao.InsteadPayDetailDAO;
import com.zlebank.zplatform.trade.model.PojoInsteadPayDetail;
import com.zlebank.zplatform.trade.model.TxnsLogModel;
import com.zlebank.zplatform.trade.service.ITxnsLogService;
import com.zlebank.zplatform.trade.service.NotifyInsteadURLService;
import com.zlebank.zplatform.trade.service.ObserverListService;
import com.zlebank.zplatform.trade.service.UpdateInsteadService;
import com.zlebank.zplatform.trade.service.UpdateSubject;

/**
 * Class Description
 *
 * @author Luxiaoshuai
 * @version
 * @date 2016年3月16日 下午3:59:29
 * @since 
 */
@Service
public class UpdateInsteadServiceImpl implements UpdateInsteadService, UpdateSubject ,ApplicationListener<ContextRefreshedEvent> {
    
    private static final Log log = LogFactory.getLog(UpdateInsteadServiceImpl.class);
    
    @Autowired
    private InsteadPayBatchDAO insteadPayBatchDAO;
    
    @Autowired
    private InsteadPayDetailDAO insteadPayDetailDAO;
    
    @Autowired
    private AccEntryService accEntryService;
    
    @Autowired
    private NotifyInsteadURLService notifyInsteadURLService;
    
    @Autowired
    private ITxnsLogService txnsLogService;
    
    @Autowired
    private MemberService memberService;
    @Autowired
    private CoopInstiDAO coopInstiDAO;
    /**
     *  更新状态和记账
     * @param data
     */
    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW,rollbackFor=Throwable.class)
    public void update(UpdateData data) {
        List<UpdateSubject> observerList = ObserverListService.getInstance().getObserverList();
        for (UpdateSubject subject : observerList) {
        	log.info(subject.getBusiCode());
        }
        PojoInsteadPayDetail detail = insteadPayDetailDAO.getDetailByTxnseqno(data.getTxnSeqNo());
        if (detail == null) {
            log.error("没有找到需要记账的流水");
            return;
        }
        if ("00".equals(data.getResultCode())) {
            detail.setRespCode(data.getResultCode());
            detail.setStatus(InsteadPayDetailStatusEnum.TRAN_FINISH.getCode());
        } else if("09".equals(data.getResultCode())) {
            detail.setRespCode("09");
            detail.setStatus(InsteadPayDetailStatusEnum.TRAN_FAILED.getCode());
        }else if("02".equals(data.getResultCode())) {
            detail.setRespCode("09");
            detail.setStatus(InsteadPayDetailStatusEnum.TRAN_FAILED.getCode());
        }
        detail.setRespMsg(data.getResultMessage());
        insteadPayDetailDAO.merge(detail);
        //TxnsLogModel txnsLog = txnsLogService.getTxnsLogByTxnseqno(detail.getTxnseqno());
        TradeInfo tradeInfo = new TradeInfo();
        tradeInfo.setPayMemberId(detail.getMerId());
        tradeInfo.setAmount(new BigDecimal(detail.getAmt()));
        tradeInfo.setCharge(new BigDecimal(detail.getTxnfee()));
        tradeInfo.setTxnseqno(detail.getTxnseqno());
        PojoMember member = memberService.getMbmberByMemberId(detail.getMerId(), null);
        PojoCoopInsti pojoCoopInsti = coopInstiDAO.get(member.getInstiId());
        tradeInfo.setCoopInstCode(pojoCoopInsti.getInstiCode());
        EntryEvent entryEvent = null;
        if ("00".equals(data.getResultCode())) {
            tradeInfo.setBusiCode("70000001");
            tradeInfo.setChannelId(data.getChannelCode());
            entryEvent = EntryEvent.TRADE_SUCCESS;
        } else if("02".equals(data.getResultCode())){
        	tradeInfo.setBusiCode("70000001");
            entryEvent = EntryEvent.AUDIT_REJECT;
        }else {
            tradeInfo.setBusiCode("70000001");
            entryEvent = EntryEvent.TRADE_FAIL;
        }
        TxnsLogModel txnsLog = txnsLogService.getTxnsLogByTxnseqno(data.getTxnSeqNo());
       
    	try {
			log.info(JSON.toJSONString(tradeInfo));
			accEntryService.accEntryProcess(tradeInfo, entryEvent);
			txnsLog.setApporderstatus(AccStatusEnum.Finish.getCode());
            txnsLog.setApporderinfo("代付账务成功");
		} catch (AccBussinessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			txnsLog.setApporderstatus(AccStatusEnum.AccountingFail.getCode());
            txnsLog.setApporderinfo(e1.getMessage());
		} catch (AbstractBusiAcctException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			txnsLog.setApporderstatus(AccStatusEnum.AccountingFail.getCode());
            txnsLog.setApporderinfo(e1.getMessage());
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			txnsLog.setApporderstatus(AccStatusEnum.AccountingFail.getCode());
            txnsLog.setApporderinfo(e1.getMessage());
		}
        //更新交易流水应用方信息
        txnsLogService.updateAppStatus(data.getTxnSeqNo(), txnsLog.getApporderstatus(), txnsLog.getApporderinfo());
        try {
			// 如果批次已经全部处理完毕，则添加到通知
			if (insteadPayDetailDAO.isBatchProcessFinished(detail.getInsteadPayBatch().getId())) {
			    notifyInsteadURLService.addInsteadPayTask(detail.getInsteadPayBatch().getId());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }


    /**
     * 得到业务代码
     */
    @Override
    public String getBusiCode() {
        return TransferBusiTypeEnum.INSTEAD.getCode();
    }


    /**
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
    	if (event.getApplicationContext().getParent() == null) {
            ObserverListService.getInstance().add(this);
        }
    }
}
