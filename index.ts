import { NativeModules } from 'react-native'

const { AmazonIap } = NativeModules


export interface User {
    userId: string,
    marketplace: string
}

export function getUser() : Promise<User> {
    return AmazonIap.getUser()
}