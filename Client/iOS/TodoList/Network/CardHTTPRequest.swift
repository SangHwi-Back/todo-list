//
//  CardHTTPRequest.swift
//  TodoList
//
//  Created by 백상휘 on 2022/04/05.
//

import Foundation

/// 카드 뿐만 아니라 다른 상황에서도 사용할 수 있는 GET/POST 요청을 할 수 있습니다.
///
/// - doGetRequest(url:URL,:[String:Any]?,completionHandler:(Codable?)->Void)
/// - doPostRequest(url:URL,:Data?,completionHandler:(Codable?)->Void)
class CardHTTPRequest: SessionConfiguration {
    
    /// HTTP GET 요청을 보냅니다.
    ///
    /// - url: Request를 보낼 URL입니다.
    /// - parameter: Request 뒤에 파라미터로 보낼 딕셔너리 입니다. (예: https://getRequest.com?objectKey=myKey 의 objectKey:myKey)
    /// - completionHandler: 요청 후 Data객체를 (혹은 nil인) 호출하는 클로저입니다.
    func doGetRequest(url: URL, parameter: [String:String]?, completionHandler: @escaping (Data?)->Void) {
        var queryItems = [URLQueryItem]()
        if let parameter = parameter {
            for param in parameter {
                queryItems.append(URLQueryItem(name: param.key, value: param.value))
            }
        }
        var urlComp = URLComponents(url: url, resolvingAgainstBaseURL: false)
        urlComp?.queryItems = queryItems
        
        if let url = urlComp?.url {
            getRequestHandler(url: url) { request in
                self.session.dataTask(with: request) { data, response, error in
                    guard let data = data else {
                        completionHandler(nil)
                        return
                    }
                    
                    completionHandler(data)
                }.resume()
            }
        } else {
            completionHandler(nil)
        }
    }
    
    func doGetRequest(parameter: [String:String]?, completionHandler: @escaping (Data?)->Void) {
        guard let url = URL(string: urlString) else {
            completionHandler(nil)
            return
        }
        
        var queryItems = [URLQueryItem]()
        if let parameter = parameter {
            for param in parameter {
                queryItems.append(URLQueryItem(name: param.key, value: param.value))
            }
        }
        var urlComp = URLComponents(url: url, resolvingAgainstBaseURL: false)
        urlComp?.queryItems = queryItems
        
        if let url = urlComp?.url {
            urlString = url.absoluteString
            getCurrentRequestHandler { request in
                self.session.dataTask(with: request) { data, response, error in
                    guard let data = data else {
                        completionHandler(nil)
                        return
                    }
                    
                    completionHandler(data)
                }.resume()
            }
        } else {
            completionHandler(nil)
        }
    }
    
    /// HTTP POST 요청을 보냅니다.
    ///
    /// - url: Request를 보낼 URL입니다.
    /// - parameter: Request body에 보낼 이진 데이터(Data 구조체)입니다.
    /// - completionHandler: 요청 후 Data객체를 (혹은 nil인) 호출하는 클로저입니다.
    func doPostRequest(url: URL, _ paramData: Data? = nil, completionHandler: @escaping (Data?)->Void) {
        getRequestHandler(url: url) { request in
            self.session.uploadTask(with: request, from: paramData) { data, response, error in
                guard let data = data else {
                    completionHandler(nil)
                    return
                }
                
                completionHandler(data)
            }.resume()
        }
    }
    
    func doPostRequest(_ paramData: Data? = nil, completionHandler: @escaping (Data?)->Void) {
        getCurrentRequestHandler { request in
            self.session.uploadTask(with: request, from: paramData) { data, response, error in
                guard let data = data else {
                    completionHandler(nil)
                    return
                }
                
                completionHandler(data)
            }.resume()
        }
    }
}