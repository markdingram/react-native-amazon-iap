import { NativeModules } from 'react-native'

const { AmazonIap } = NativeModules


export interface UserData {
    userId: string,
    marketplace: string
}

export interface UserResponse {
    requestStatus: 'SUCCESSFUL' | 'FAILED' | 'NOT_SUPPORTED'
    userData: UserData
}

export interface Product {
    sku: string
    productType: string
    description: string
    price: string
    smallIconUrl: string
    title: string
    coinsRewardAmount: number
}

export interface ProductsResponse {
    requestStatus: 'SUCCESSFUL' | 'FAILED' | 'NOT_SUPPORTED'
    unavailableSkus: string[]
    productData: Product[]
}

export interface Receipt {
    receiptId: string
    sku: string
    itemType: string
    purchaseDate: string,
    endDate: string
}

export interface PurchaseResponse {
    requestStatus: 'SUCCESSFUL' | 'FAILED' | 'INVALID_SKU' | 'ALREADY_PURCHASED' | 'NOT_SUPPORTED'

    userData: UserData
    receipt: Receipt
}

export interface PurchaseUpdatesResponse {
    requestStatus: 'SUCCESSFUL' | 'FAILED' | 'NOT_SUPPORTED'

    userData: UserData
    receipts: Receipt[]

    hasMore: boolean
}

export function getUser() : Promise<UserResponse> {
    return AmazonIap.getUser()
}

export function getProducts(skus: string[]) : Promise<ProductsResponse> {
    return AmazonIap.getProducts(skus)
}

export function getPurchaseUpdates() : Promise<PurchaseUpdatesResponse> {
    return AmazonIap.getPurchaseUpdates()
}

export function purchase(sku: string) : Promise<PurchaseResponse> {
    return AmazonIap.purchase(sku)
}

export function notifyFulfillment(receiptId: String, fulfillmentResult: 'FULFILLED' | 'UNAVAILABLE') {
    AmazonIap.notifyFulfillment(receiptId, fulfillmentResult)
}