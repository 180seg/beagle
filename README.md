# Beagle
Beagle is an AWS policy judge.
![image](https://user-images.githubusercontent.com/12648924/191527413-ac88cba4-cfaf-4cd0-a311-29cd68800a10.png)

Based on a list of [forbidden actions](resources/forbidden-actions) which are basically policy laws he will evaluate your policy to identify if it's not breaking any of them.

# Concepts
Beagle is really simple and relies on two simple concepts.
## Laws
Set of aws policies containg actions and resources that an entity is not allowed to perform

## Policies
One or more policies that are going to be judge by the defined laws.

# How Beagle takes his decision
Beagle uses AWS [simluate custom policy API](https://docs.aws.amazon.com/IAM/latest/APIReference/API_SimulateCustomPolicy.html) to determine if the provided policies are not breaking any law.

# Example
We have the following policy which allows an entity to perform the `PutObject` action in any bucket

```json
{
  "Version":"2012-10-17",
    "Statement":[
    {
      "Effect":"Allow",
      "Action":[
        "s3:PutObject"
      ],
      "Resource":"*"
    }
  ]
}
```

and the law above that does not allow any any action on s3 in a specific resource.

```json
{
  "Version":"2012-10-17",
    "Statement":[
    {
      "Effect":"Deny",
      "Action":[
        "s3:PutObject"
        "s3:GetObject"
      ],
      "Resource":[
        "arn:aws:s3:::bucket-with-secrets/*"
      ]
    }
  ]
}
```

Beagle will decide if the policy is not breaking the laws. 
To do that just save the previous policy on a file `policy.json` and the law in a directory of laws such as `laws/s3-secret-bucket.json` and then just run the following command.

```bash
clojure -m beagle.cli check --policy policy.json -l laws/
You can not trust in it.
```
