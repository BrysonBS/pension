import JSEncrypt from 'jsencrypt/bin/jsencrypt.min'
import { getPublicKey } from '@/api/login'

// 密钥对生成 http://web.chacuo.net/netrsakeypair
// 加密
export async function encrypt(txt) {
  let publicKey = sessionStorage.getItem("publicKey")
  if (!publicKey) {
    let response = await getPublicKey();
    publicKey = response.data
    sessionStorage.setItem("publicKey", publicKey);
  }
  const encryptor = new JSEncrypt()
  encryptor.setPublicKey(publicKey) // 设置公钥
  return encryptor.encrypt(txt) // 对数据进行加密
}

// 解密
export function decrypt(txt) {
  const encryptor = new JSEncrypt()
  encryptor.setPrivateKey("") // 设置私钥
  return encryptor.decrypt(txt) // 对数据进行解密
}

