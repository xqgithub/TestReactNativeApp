import React, { Component } from 'react'
import { View,
Text,
Image,
StyleSheet,
NativeModules,
DeviceEventEmitter,
ToastAndroid
} from 'react-native'


export default class extends Component {

   /**
    * 调用原生代码
    */
    skipNativeCall() {
       let phone = '13554298369';
       NativeModules.commModule.rnCallNative(phone);
    }

   /**
    * 接收原生调用
    */
    componentDidMount(){
         DeviceEventEmitter.addListener('nativeCallRn',(msg)=>{
         title = "React Native界面,收到数据：" + msg;
          ToastAndroid.show("发送成功:"+title, ToastAndroid.SHORT);
     })
    }

       /**
        * Callback 通信方式
        */
        callbackComm(msg) {
            NativeModules.commModule.rnCallNativeFromCallback(msg,(result) => {
                 ToastAndroid.show("CallBack收到消息:" + result, ToastAndroid.SHORT);
            })

        }
    /**
     * Promise 通信方式
     */
    promiseComm(msg) {
        NativeModules.commModule.rnCallNativeFromPromise(msg).then(
            (result) =>{
                ToastAndroid.show("Promise收到消息:" + result, ToastAndroid.SHORT)
            }
        ).catch((error) =>{
        console.log(error);
        ToastAndroid.show("Promise通信方式error----->:" + error, ToastAndroid.SHORT);
        });
    }

  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.text}>
          Hello React Native!
        </Text>
         <Text style={styles.text} onPress={this.skipNativeCall.bind(this)}>
            跳转到拨号界面
         </Text>
        <Text style={styles.text} onPress={this.callbackComm.bind(this,'我是第一剑士索隆')}>
          Callback通信方式
        </Text>
         <Text style={styles.text} onPress={this.promiseComm.bind(this,'我是第一个美女娜美')}>
          Promise通信方式
         </Text>
      <Image source={require('../images/ic_1.png')} />
      </View>
    );
  }
}
const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#209bf4'
  },
  text: {
    fontSize: 20,
    textAlign: 'center',
    color: '#333333'
  }
})