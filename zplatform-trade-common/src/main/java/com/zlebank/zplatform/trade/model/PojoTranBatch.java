/* 
 * PojoTranBatch.java  
 * 
 * version TODO
 *
 * 2016年3月8日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.trade.model;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * 划拨批次
 *
 * @author Luxiaoshuai
 * @version
 * @date 2016年3月8日 下午3:20:31
 * @since 1.3.0
 */
@Entity
@Table(name = "T_TRAN_BATCH")
public class PojoTranBatch {
    /**标示**/
    private Long tid;
    /**划拨批次号**/
     private String tranBatchNo;
    /**总笔数**/
    private Long totalCount;
    /**总金额**/
    private Long totalAmt;
    /**审核通过笔数**/
    private Long approveCount;
    /**审核通过金额**/
    private BigDecimal approveAmt;
    /**审核拒绝笔数**/
    private Long refuseCount;
    /**审核拒绝金额**/
    private BigDecimal refuseAmt;
    /**"""状态（01：未审核02：部分审核通过03：全部审核通过**/
    private String status;
    /**"申请时间"**/
    private Date applyTime;
    /**"全部通过完成时间"**/
    private Date approveFinishTime;
    /**"业务类型（00：代付01：提现02：退款）"**/
    private String busiType;
    /**"业务批次ID"**/
    private String busiBatchId;
    /**"银行转账完成时间"**/
    private Date finishTime; 
    /**待审核笔数**/
    private Long waitApproveCount;
    /**待审核金额**/
    private Long waitApproveAmt;
    /**银行转账批次号**/
    private List<PojoBankTransferBatch> bankTransferBatchs;
    @GenericGenerator(name = "id_gen", strategy = "enhanced-table", parameters = {
            @Parameter(name = "table_name", value = "T_C_PRIMAY_KEY"),
            @Parameter(name = "value_column_name", value = "NEXT_ID"),
            @Parameter(name = "segment_column_name", value = "KEY_NAME"),
            @Parameter(name = "segment_value", value = "T_TRANSFER_BATCH_ID"),
            @Parameter(name = "increment_size", value = "1"),
            @Parameter(name = "optimizer", value = "pooled-lo") })
    @Id
    @GeneratedValue(generator = "id_gen")
    @Column(name = "TID", unique = true, nullable = false, precision = 12, scale = 0)
    public Long getTid() {
        return tid;
    }
    public void setTid(Long tid) {
        this.tid = tid;
    }
    @Column(name = "TRAN_BATCH_NO")
    public String getTranBatchNo() {
        return tranBatchNo;
    }
    public void setTranBatchNo(String tranBatchNo) {
        this.tranBatchNo = tranBatchNo;
    }
    @Column(name = "TOTAL_COUNT")
    public Long getTotalCount() {
        return totalCount;
    }
    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
    @Column(name = "TOTAL_AMT")
    public Long getTotalAmt() {
        return totalAmt;
    }
    public void setTotalAmt(Long totalAmt) {
        this.totalAmt = totalAmt;
    }
    @Column(name = "APPROVE_COUNT")
    public Long getApproveCount() {
        return approveCount;
    }
    public void setApproveCount(Long approveCount) {
        this.approveCount = approveCount;
    }
    @Column(name = "APPROVE_AMT")
    public BigDecimal getApproveAmt() {
        return approveAmt;
    }
    public void setApproveAmt(BigDecimal approveAmt) {
        this.approveAmt = approveAmt;
    }
    @Column(name = "REFUSE_COUNT")
    public Long getRefuseCount() {
        return refuseCount;
    }
    public void setRefuseCount(Long refuseCount) {
        this.refuseCount = refuseCount;
    }
    @Column(name = "REFUSE_AMT")
    public BigDecimal getRefuseAmt() {
        return refuseAmt;
    }
    public void setRefuseAmt(BigDecimal refuseAmt) {
        this.refuseAmt = refuseAmt;
    }
    @Column(name = "STATUS")
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    @Column(name = "APPLY_TIME")
    public Date getApplyTime() {
        return applyTime;
    }
    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }
    @Column(name = "APPROVE_FINISH_TIME")
    public Date getApproveFinishTime() {
        return approveFinishTime;
    }
    public void setApproveFinishTime(Date approveFinishTime) {
        this.approveFinishTime = approveFinishTime;
    }
    @Column(name = "BUSI_TYPE")
    public String getBusiType() {
        return busiType;
    }
    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }
    @Column(name = "BUSI_BATCH_ID")
    public String getBusiBatchId() {
        return busiBatchId;
    }
    public void setBusiBatchId(String busiBatchId) {
        this.busiBatchId = busiBatchId;
    }
    @Column(name = "FINISH_TIME")
    public Date getFinishTime() {
        return finishTime;
    }
    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }
    @Column(name = "WAIT_APPROVE_COUNT")
    public Long getWaitApproveCount() {
        return waitApproveCount;
    }
    public void setWaitApproveCount(Long waitApproveCount) {
        this.waitApproveCount = waitApproveCount;
    }
    @Column(name = "WAIT_APPROVE_AMT")
    public Long getWaitApproveAmt() {
        return waitApproveAmt;
    }
    public void setWaitApproveAmt(Long waitApproveAmt) {
        this.waitApproveAmt = waitApproveAmt;
    }
    @ManyToMany
    @JoinTable(name="")
    public List<PojoBankTransferBatch> getBankTransferBatchs() {
        return bankTransferBatchs;
    }
    public void setBankTransferBatchs(List<PojoBankTransferBatch> bankTransferBatchs) {
        this.bankTransferBatchs = bankTransferBatchs;
    }
}