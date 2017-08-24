package com.global.api.paymentMethods;

import com.global.api.ServicesContainer;
import com.global.api.builders.AuthorizationBuilder;
import com.global.api.entities.*;
import com.global.api.entities.enums.PaymentMethodType;
import com.global.api.entities.enums.TransactionType;
import com.global.api.entities.exceptions.ApiException;
import com.global.api.entities.exceptions.UnsupportedTransactionException;
import com.global.api.gateways.IRecurringGateway;
import com.global.api.services.RecurringService;

import java.math.BigDecimal;
import java.util.List;

public class RecurringPaymentMethod extends RecurringEntity<RecurringPaymentMethod> implements  IPaymentMethod, IChargable, IAuthable, IVerifiable, IRefundable  {
    private Address address;
    private String commercialIndicator;
    private String customerKey;
    private String expirationDate;
    private String nameOnAccount;
    private IPaymentMethod paymentMethod;
    private String paymentType;
    private boolean preferredPayment;
    private String status;
    private String taxType;

    public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
        this.address = address;
    }
    public String getCommercialIndicator() {
        return commercialIndicator;
    }
    public void setCommercialIndicator(String commercialIndicator) {
        this.commercialIndicator = commercialIndicator;
    }
    public String getCustomerKey() {
        return customerKey;
    }
    public void setCustomerKey(String customerKey) {
        this.customerKey = customerKey;
    }
    public String getExpirationDate() {
        return expirationDate;
    }
    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
    public String getNameOnAccount() {
        return nameOnAccount;
    }
    public void setNameOnAccount(String nameOnAccount) {
        this.nameOnAccount = nameOnAccount;
    }
    public IPaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(IPaymentMethod paymentMethod) throws ApiException {
        IRecurringGateway client = ServicesContainer.getInstance().getRecurring();
        if(client.supportsUpdatePaymentDetails())
            this.paymentMethod = paymentMethod;
        else throw new UnsupportedTransactionException();
    }
    public PaymentMethodType getPaymentMethodType() {
        return PaymentMethodType.Recurring;
    }
    public String getPaymentType() {
        return paymentType;
    }
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
    public boolean isPreferredPayment() {
        return preferredPayment;
    }
    public void setPreferredPayment(boolean preferredPayment) {
        this.preferredPayment = preferredPayment;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getTaxType() {
        return taxType;
    }
    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public RecurringPaymentMethod() {
        this(null, null);
    }
    public RecurringPaymentMethod(IPaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    public RecurringPaymentMethod(String customerKey, String paymentId) {
        this.customerKey = customerKey;
        this.key = paymentId;
        paymentType = "Credit Card";
    }

    public AuthorizationBuilder authorize() {
        return authorize(null);
    }
    public AuthorizationBuilder authorize(BigDecimal amount) {
        return new AuthorizationBuilder(TransactionType.Auth, this).withAmount(amount).withOneTimePayment(true);
    }

    public AuthorizationBuilder charge() {
        return charge(null);
    }
    public AuthorizationBuilder charge(BigDecimal amount) {
        return new AuthorizationBuilder(TransactionType.Sale, this).withAmount(amount).withOneTimePayment(true);
    }

    public AuthorizationBuilder refund() {
        return refund(null);
    }
    public AuthorizationBuilder refund(BigDecimal amount) {
        return new AuthorizationBuilder(TransactionType.Refund, this).withAmount(amount);
    }

    public AuthorizationBuilder verify() {
        return new AuthorizationBuilder(TransactionType.Verify, this);
    }

    public RecurringPaymentMethod create() throws ApiException {
        return RecurringService.create(this, RecurringPaymentMethod.class);
    }

    public void delete() throws ApiException {
        delete(false);
    }
    public void delete(boolean force) throws ApiException {
        try{
            RecurringService.delete(this, RecurringPaymentMethod.class, force);
        }
        catch(ApiException e) {
            throw new ApiException("Failed to delete payment method, see inner exception for more details.", e);
        }
    }

    public static RecurringPaymentMethod find(String id) throws ApiException {
        checkSupportsRetrieval();

        List<RecurringPaymentMethod> response = RecurringService.search(RecurringPaymentMethodCollection.class)
                .addSearchCriteria("paymentMethodIdentifier", id)
                .execute();
        if(response.size() > 0) {
            RecurringPaymentMethod entity = response.get(0);
            if (entity != null)
                return RecurringService.get(entity.getKey(), RecurringPaymentMethod.class);
        }
        return null;
    }

    public static List<RecurringPaymentMethod> findAll() throws ApiException {
        checkSupportsRetrieval();
        return RecurringService.search(RecurringPaymentMethodCollection.class).execute();
    }

    public static RecurringPaymentMethod get(String key) throws ApiException {
        checkSupportsRetrieval();
        return RecurringService.get(key, RecurringPaymentMethod.class);
    }

    public void saveChanges() throws ApiException {
        try{
            RecurringService.edit(this, RecurringPaymentMethod.class);
        }
        catch (ApiException e) {
            throw new ApiException("Update failed, see inner exception for more details", e);
        }
    }

    public Schedule addSchedule(String scheduleId) {
        Schedule schedule = new Schedule(customerKey, key);
        schedule.setId(scheduleId);
        return schedule;
    }
}