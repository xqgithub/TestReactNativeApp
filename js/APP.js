import React, { Component } from 'react'
import { View,
Text,
StyleSheet,
NativeModules,
DeviceEventEmitter,
ToastAndroid
} from 'react-native'
export default class extends Component {

  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.text}>
          Hello React Native!
        </Text>
         <Text style={styles.text}>
            跳转到拨号界面
         </Text>
        <Text style={styles.text}>
          Callback通信方式
        </Text>
         <Text style={styles.text}>
          Promise通信方式
         </Text>
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